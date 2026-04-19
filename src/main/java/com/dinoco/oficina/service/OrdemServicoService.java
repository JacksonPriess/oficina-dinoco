package com.dinoco.oficina.service;

import com.dinoco.oficina.dto.*;
import com.dinoco.oficina.entity.*;
import com.dinoco.oficina.enums.StatusItemServico;
import com.dinoco.oficina.enums.StatusOS;
import com.dinoco.oficina.enums.TipoMovimentacao;
import com.dinoco.oficina.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrdemServicoService {

    private final OrdemServicoRepository osRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemOsProdutoRepository itemProdutoRepository;
    private final ServicoRepository servicoRepository;
    private final ItemOsServicoRepository itemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    @Transactional
    public OrdemServicoResponseDto abrirOs(OrdemServicoRequestDto dto) {

        if ( osRepository.existeOsAtivaParaVeiculo(dto.veiculoId()) ) {
            throw new IllegalStateException("Já existe uma Ordem de Serviço aberta ou em execução para este veículo.");
        }

        OrdemServico os = new OrdemServico();
        os.setCliente(clienteRepository.getReferenceById(dto.clienteId()));
        os.setVeiculo(veiculoRepository.getReferenceById(dto.veiculoId()));
        os.setQuilometragemEntrada(dto.quilometragemEntrada());
        os.setReclamacaoCliente(dto.reclamacaoCliente());
        os.setStatus(StatusOS.RECEBIDA);
        OrdemServico osAberta = osRepository.save(os);
        return mapearParaResponse(osAberta);
    }

    /**
     * iniciarDiagnostico() -> alterar para EM_DIAGNOSTICO
     * concluirDiagnostico() -> alterar para AGUARDANDO_ORCAMENTO    ... obs precisa ter ao menos um item na os.
     * enviarOrcamento() -> alterar para AGUARDANDO_APROVACAO        ... valor da os precisa ser maior que zero
     * reprovarOrcamento() -> alterar para REPROVAR ... precisa informar data de da OS.
     * aprovarOrcamento() ->  Logica mais complexa :
     *  1° passo : Sistema vai reservar a peça no estoque para todos os itens da OS.
     *  2° passo : Sistema verificará se existe quantidade real, ( getQuantidadeDisponivel > 0 ) para todos os itens de produto.
     *         i - Se tiver saldo para todos os itens de produto, alterar status para AGUARDANDO_EXECUCAO.
     *         ii - Se não, altera o status para AGUARDANDO_FORNECEDOR. e aqui o atendente precisa agir, e vai fazer movimento de entrada da peca no estoque
     *  refreshNaOS -> aqui apenas será possível clicar, quando a OS estiver em AGUARDANDO_FORNECEDOR, pois o sistema vai calcular novamente apenas os itens ( getQuantidadeDisponivel > 0 ), e se a peça nova chegou no estoque, alterar status para AGUARDANDO_EXECUCAO
     *
     *  iniciarExecucaoOS() ->   Alterar status da OS para EM_EXECUCAO, precisa atualizar o estoque para dar baixa ba peca.
     *  iniciarExecucaoItemServicoOS() -> Alterar status do item de servico para EM_ANDAMENTO
     *  concluirItemServicoOS -> Alterar status do item de servico para CONCLUIDO
     *
     *  finalizarExecucaoOS() ->  Alterar status da OS para FINALIZADA, aqui quer dizer que todos os servicos foram realizados e o carro está pronto.
     *  concluirOS -> Alterar status da OS para ENTREGUE
     */

    @Transactional
    public void iniciarDiagnostico(Long osId) {
        OrdemServico os = buscarOuFalhar(osId);
        validarStatus(os, StatusOS.RECEBIDA);
        os.setStatus(StatusOS.EM_DIAGNOSTICO);
        osRepository.save(os);
    }

    @Transactional
    public void concluirDiagnostico(Long osId, String laudoTecnico) {
        OrdemServico os = buscarOuFalhar(osId);
        validarStatus(os, StatusOS.EM_DIAGNOSTICO);
        os.setLaudoTecnico(laudoTecnico);
        os.setStatus(StatusOS.AGUARDANDO_ORCAMENTO);
        osRepository.save(os);
    }

    @Transactional
    public void enviarOrcamento(Long osId) {
        OrdemServico os = buscarOuFalhar(osId);

        boolean temItemProdutoSemPreco = os.getItensProduto().stream()
                .anyMatch(item -> item.getValorUnitarioVenda().compareTo(BigDecimal.ZERO) <= 0);

        boolean temItemServicoSemValor = os.getItensServico().stream()
                .anyMatch(item -> item.getValorCobrado().compareTo(BigDecimal.ZERO) <= 0);

        if ( temItemProdutoSemPreco || temItemServicoSemValor ) {
            throw new IllegalStateException("Existem itens da OS sem valor R$.");
        }

        os.setStatus(StatusOS.AGUARDANDO_APROVACAO);
        osRepository.save(os);
    }

    @Transactional
    public void reprovarOrcamento(Long osId) {
        OrdemServico os = buscarOuFalhar(osId);
        validarStatus(os, StatusOS.AGUARDANDO_APROVACAO);
        os.setStatus(StatusOS.REPROVADA);
        //TODO - Criar uma data de encerramento geral da OS, saida não fica legal
        os.setDataSaida(LocalDateTime.now());
        osRepository.save(os);
    }

    @Transactional
    public void aprovarOrcamento(Long osId) {
        OrdemServico os = buscarOuFalhar(osId);
        validarStatus(os, StatusOS.AGUARDANDO_APROVACAO);
        processarReservaEstoque(os);
        boolean temTudoNoEstoque = os.getItensProduto().stream()
                .allMatch(item -> item.getProduto().getQuantidadeDisponivel().compareTo(BigDecimal.ZERO) >= 0);

        if (temTudoNoEstoque) {
            os.setStatus(StatusOS.AGUARDANDO_EXECUCAO);
        } else {
            os.setStatus(StatusOS.AGUARDANDO_FORNECEDOR);
        }

        osRepository.save(os);
    }

    @Transactional
    public void verificarDisponibilidadePecas(Long osId) {
        OrdemServico os = buscarOuFalhar(osId);
        validarStatus(os, StatusOS.AGUARDANDO_FORNECEDOR);

        boolean agoraTemTudo = os.getItensProduto().stream()
                .allMatch(item -> item.getProduto().getQuantidadeDisponivel().compareTo(BigDecimal.ZERO) >= 0);

        if (agoraTemTudo) {
            os.setStatus(StatusOS.AGUARDANDO_EXECUCAO);
            osRepository.save(os);
        }
    }

    @Transactional
    public void iniciarExecucaoOS(Long osId) {
        OrdemServico os = buscarOuFalhar(osId);
        validarStatus(os, StatusOS.AGUARDANDO_EXECUCAO);
        efetivarBaixaEstoque(os);
        os.setStatus(StatusOS.EM_EXECUCAO);
        osRepository.save(os);
    }

    @Transactional
    public void iniciarItemServico(Long itemServicoId) {
        ItemOsServico item = itemServicoRepository.findById(itemServicoId)
                .orElseThrow(() -> new RuntimeException("Item de serviço não encontrado"));
        item.setStatusItem(StatusItemServico.EM_ANDAMENTO);
        item.setDataInicio(LocalDateTime.now());
        itemServicoRepository.save(item);
    }

    @Transactional
    public void concluirItemServico(Long itemServicoId) {
        ItemOsServico item = itemServicoRepository.findById(itemServicoId)
                .orElseThrow(() -> new RuntimeException("Item de serviço não encontrado"));
        item.setStatusItem(StatusItemServico.CONCLUIDO);
        item.setDataFim(LocalDateTime.now());
        itemServicoRepository.save(item);
    }

    @Transactional
    public void finalizarExecucaoOS(Long osId) {
        OrdemServico os = buscarOuFalhar(osId);
        validarStatus(os, StatusOS.EM_EXECUCAO);

        // Regra de Negócio: Não pode finalizar se houver serviços não concluídos
        boolean temServicoPendente = os.getItensServico().stream()
                .anyMatch(item -> item.getStatusItem() != StatusItemServico.CONCLUIDO);

        if (temServicoPendente) {
            throw new IllegalStateException("Não é possível finalizar a OS. Existem serviços pendentes ou em andamento.");
        }

        os.setStatus(StatusOS.FINALIZADA);
        osRepository.save(os);
    }

    @Transactional
    public void entregarVeiculo(Long osId) {
        OrdemServico os = buscarOuFalhar(osId);
        validarStatus(os, StatusOS.FINALIZADA);
        //TODO - Faturamento - Nota de Servico
        os.setStatus(StatusOS.ENTREGUE);
        os.setDataSaida(LocalDateTime.now());
        osRepository.save(os);
    }

    private void validarStatus(OrdemServico os, StatusOS statusEsperado) {
        if (os.getStatus() != statusEsperado) {
            throw new IllegalStateException("Operação inválida para o status atual da OS: " + os.getStatus());
        }
    }

    @Transactional
    public void adicionarItemProduto(Long osId, ItemProdutoAdicionarDto dto) {
        OrdemServico os = buscarOuFalhar(osId);

        if ( !os.getStatus().equals(StatusOS.EM_DIAGNOSTICO) ) {
            throw new IllegalArgumentException("Inicie o diagnóstico da OS antes de adicionar itens de produto.");
        }

        //TODO - Quando o produto ainda não existir ele deve ser incluido ( com dados básicos e tudo zerado, depois o atendente atualizará o item)
        Produto produto = produtoRepository.findById(dto.produtoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        ItemOsProduto item = new ItemOsProduto();
        item.setOrdemServico(os);
        item.setProduto(produto);
        item.setQuantidade(dto.quantidade());
        item.setValorUnitarioVenda(dto.valorUnitarioVenda());
        item.setValorTotal(dto.quantidade().multiply(dto.valorUnitarioVenda()));
        os.getItensProduto().add(item);
        itemProdutoRepository.save(item);
        recalcularTotais(os);
    }

    //TODO - Implementar alteração do item do produto, para alterar o valor unitário de venda e recalcular totais(orcamento).
    @Transactional
    public void alterarItemProduto() {}

    //TODO - Implementar remoção do item do produto -> recalcular totais(orcamento).
    @Transactional
    public void removerItemProduto() {}

    @Transactional
    public void adicionarItemServico(Long osId, ItemServicoAdicionarDto dto) {
        OrdemServico os = buscarOuFalhar(osId);

        if ( !os.getStatus().equals(StatusOS.EM_DIAGNOSTICO) ) {
            throw new IllegalArgumentException("Inicie o diagnóstico da OS antes de adicionar itens de serviço.");
        }

        // Busca o serviço base no catálogo
        Servico servico = servicoRepository.findById(dto.servicoId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado no catálogo"));

        ItemOsServico item = new ItemOsServico();
        item.setOrdemServico(os);
        item.setServico(servico);
        item.setValorCobrado(dto.valorCobrado());
        item.setStatusItem(StatusItemServico.PENDENTE);

        // Se o atendente já souber quem é o mecânico, já vincula aqui
        if (dto.mecanicoId() != null) {
            Funcionario mecanico = funcionarioRepository.findById(dto.mecanicoId())
                    .orElseThrow(() -> new RuntimeException("Mecânico não encontrado"));
            item.setMecanico(mecanico);
        }
        os.getItensServico().add(item);
        itemServicoRepository.save(item);
        recalcularTotais(os);
    }

    //TODO - Implementar alteração do item do servico, caso ocorra necessidade de alterar valor do servico e recalcular totais(orcamento).
    @Transactional
    public void alterarItemServico() {}

    //TODO - Implementar remoção do item do servico -> recalcular totais(orcamento).
    @Transactional
    public void removerItemServico() {}

    //TODO - IMPORTANT
    @Transactional
    public void alterarStatus(Long osId, StatusOS novoStatus) {

    }

    private void recalcularTotais(OrdemServico os) {
        BigDecimal totalProdutos = os.getItensProduto().stream()
                .map(ItemOsProduto::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalServicos = os.getItensServico().stream()
                .map(ItemOsServico::getValorCobrado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        os.setValorTotalProdutos(totalProdutos);
        os.setValorTotalServicos(totalServicos);
        // Valor do Orçamento
        os.setValorTotalOs(totalProdutos.add(totalServicos).subtract(os.getValorDesconto()).max(BigDecimal.ZERO));
        osRepository.save(os);
    }

    private void processarReservaEstoque(OrdemServico os) {
        for (ItemOsProduto item : os.getItensProduto()) {
            Produto p = item.getProduto();
            p.setQuantidadeReservada(p.getQuantidadeReservada().add(item.getQuantidade()));
            produtoRepository.save(p);
            registrarAuditoria(p, item.getQuantidade(), TipoMovimentacao.RESERVA_OS, "Reserva OS: " + os.getCodigoRastreio());
        }
    }

    private void efetivarBaixaEstoque(OrdemServico os) {
        for (ItemOsProduto item : os.getItensProduto()) {
            Produto p = item.getProduto();
            p.setQuantidadeAtual(p.getQuantidadeAtual().subtract(item.getQuantidade()));
            p.setQuantidadeReservada(p.getQuantidadeReservada().subtract(item.getQuantidade()));
            produtoRepository.save(p);
            registrarAuditoria(p, item.getQuantidade(), TipoMovimentacao.BAIXA_EXECUCAO_OS, "Baixa física OS: " + os.getCodigoRastreio());
        }
    }

    private void registrarAuditoria(Produto p, BigDecimal qtd, TipoMovimentacao tipo, String obs) {
        MovimentacaoEstoque mov = new MovimentacaoEstoque(null, p, tipo, qtd, LocalDateTime.now(), obs);
        movimentacaoRepository.save(mov);
    }

    public OrdemServico buscarOuFalhar(Long id) {
        return osRepository.findById(id).orElseThrow(() -> new RuntimeException("OS não encontrada"));
    }

    private OrdemServicoResponseDto mapearParaResponse(OrdemServico ordemServico) {
        return new OrdemServicoResponseDto(
                ordemServico.getId(),
                ordemServico.getCodigoRastreio(),
                ordemServico.getCliente().getId(),
                ordemServico.getCliente().getNome(),
                ordemServico.getVeiculo().getId(),
                ordemServico.getVeiculo().getPlaca(),
                ordemServico.getReclamacaoCliente()
        );
    }
}
