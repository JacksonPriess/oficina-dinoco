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
    private final ClienteService clienteService;
    private final VeiculoService veiculoService;
    private final ProdutoRepository produtoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    @Transactional
    public OrdemServicoResponseDto abrirOs(OrdemServicoRequestDto osRequestDto) {
        validarDados(osRequestDto);
        var cliente = clienteService.buscarEntidadePorId(osRequestDto.clienteId());
        var veiculo = veiculoService.buscarEntidadePorId(osRequestDto.veiculoId());
        var ordemServico = new OrdemServico(cliente, veiculo, osRequestDto.quilometragemEntrada(), osRequestDto.reclamacaoCliente());
        return mapearParaResponse(osRepository.save(ordemServico));
    }

    private void validarDados(OrdemServicoRequestDto osRequestDto) {
        if (osRepository.existeOsAtivaParaVeiculo(osRequestDto.veiculoId())) {
            throw new IllegalStateException("Já existe uma Ordem de Serviço aberta para este veículo.");
        }
    }

    @Transactional
    public void iniciarDiagnostico(Long osId) {
        OrdemServico os = buscarOuFalhar(osId);
        validarStatusDiagnostico(os);
        os.setStatus(StatusOS.EM_DIAGNOSTICO);
        osRepository.save(os);
    }

    private void validarStatusDiagnostico(OrdemServico os) {
        if (os.getStatus() != StatusOS.RECEBIDA) {
            throw new IllegalStateException("Para iniciar um diagnóstico a OS deve estar com status RECEBIDA. Status atual da OS: " + os.getStatus());
        }
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
    public void recalcularTotais(Long osId) {
        OrdemServico os = buscarOuFalhar(osId);

        BigDecimal totalProdutos = os.getItensProduto().stream()
                .map(ItemOSProduto::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalServicos = os.getItensServico().stream()
                .map(ItemOSServico::getValorCobrado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        os.setValorTotalProdutos(totalProdutos);
        os.setValorTotalServicos(totalServicos);
        os.setValorTotalOs(totalProdutos.add(totalServicos).subtract(os.getValorDesconto()).max(BigDecimal.ZERO));

        osRepository.save(os);
    }

    private void processarReservaEstoque(OrdemServico os) {
        for (ItemOSProduto item : os.getItensProduto()) {
            Produto p = item.getProduto();
            p.setQuantidadeReservada(p.getQuantidadeReservada().add(item.getQuantidade()));
            produtoRepository.save(p);
            registrarAuditoria(p, item.getQuantidade(), TipoMovimentacao.RESERVA_OS, "Reserva OS: " + os.getCodigoRastreio());
        }
    }

    private void efetivarBaixaEstoque(OrdemServico os) {
        for (ItemOSProduto item : os.getItensProduto()) {
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
                ordemServico.getReclamacaoCliente(),
                ordemServico.getStatus().toString()
        );
    }
}