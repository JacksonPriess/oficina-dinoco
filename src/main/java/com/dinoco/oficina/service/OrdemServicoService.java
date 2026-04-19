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

        //TODO -> Validar se já existe uma OS para o cliente + placa "em aberto

        OrdemServico os = new OrdemServico();
        os.setCliente(clienteRepository.getReferenceById(dto.clienteId()));
        os.setVeiculo(veiculoRepository.getReferenceById(dto.veiculoId()));
        os.setQuilometragemEntrada(dto.quilometragemEntrada());
        os.setReclamacaoCliente(dto.reclamacaoCliente());
        os.setStatus(StatusOS.RECEBIDA);
        OrdemServico osAberta = osRepository.save(os);
        return mapearParaResponse(osAberta);
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

    @Transactional
    public void adicionarProduto(Long osId, ItemProdutoAdicionarDto dto) {
        OrdemServico os = buscarOuFalhar(osId);

        if ( !os.getStatus().equals(StatusOS.EM_DIAGNOSTICO) ) {
            throw new IllegalArgumentException("Inicie o diagnóstico da OS antes de adicionar itens.");
        }
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

    @Transactional
    public void adicionarServico(Long osId, ItemServicoAdicionarDto dto) {
        OrdemServico os = buscarOuFalhar(osId);

        if ( !os.getStatus().equals(StatusOS.EM_DIAGNOSTICO) ) {
            throw new IllegalArgumentException("Inicie o diagnóstico da OS antes de adicionar itens.");
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

    @Transactional
    public void alterarStatus(Long osId, StatusOS novoStatus) {
        OrdemServico os = buscarOuFalhar(osId);

        // Se o cliente aprovou, reservamos as peças no estoque
        if ((novoStatus == StatusOS.AGUARDANDO_EXECUCAO || novoStatus == StatusOS.AGUARDANDO_FORNECEDOR)
                && (os.getStatus() != StatusOS.AGUARDANDO_EXECUCAO && os.getStatus() != StatusOS.AGUARDANDO_FORNECEDOR)) {
            processarReservaEstoque(os);
        }

        // Se o mecânico iniciou, damos a baixa definitiva
        if (novoStatus == StatusOS.EM_EXECUCAO && os.getStatus() != StatusOS.EM_EXECUCAO) {
            efetivarBaixaEstoque(os);
        }

        // CORREÇÃO AQUI: Se a OS for reprovada após já ter tido peças reservadas, devolvemos a reserva
        if (novoStatus == StatusOS.REPROVADA &&
                (os.getStatus() == StatusOS.AGUARDANDO_EXECUCAO || os.getStatus() == StatusOS.AGUARDANDO_FORNECEDOR)) {
            estornarReservaEstoque(os);
        }

        os.setStatus(novoStatus);
        osRepository.save(os);
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

    private void estornarReservaEstoque(OrdemServico os) {
        for (ItemOsProduto item : os.getItensProduto()) {
            Produto p = item.getProduto();
            p.setQuantidadeReservada(p.getQuantidadeReservada().subtract(item.getQuantidade()));
            produtoRepository.save(p);
            registrarAuditoria(p, item.getQuantidade(), TipoMovimentacao.ESTORNO_RESERVA, "Estorno reserva OS: " + os.getCodigoRastreio());
        }
    }

    private void registrarAuditoria(Produto p, BigDecimal qtd, TipoMovimentacao tipo, String obs) {
        MovimentacaoEstoque mov = new MovimentacaoEstoque(null, p, tipo, qtd, java.time.LocalDateTime.now(), obs);
        movimentacaoRepository.save(mov);
    }

    public OrdemServico buscarOuFalhar(Long id) {
        return osRepository.findById(id).orElseThrow(() -> new RuntimeException("OS não encontrada"));
    }
}
