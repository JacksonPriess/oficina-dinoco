package com.dinoco.oficina.service;

import com.dinoco.oficina.dto.*;
import com.dinoco.oficina.entity.*;
import com.dinoco.oficina.enums.StatusItemServico;
import com.dinoco.oficina.enums.StatusOS;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdemServicoService {

    private final OrdemServicoRepository repository;
    //private final ClienteService clienteService;
    //private final VeiculoService veiculoService;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    @Transactional
    public OrdemServicoResponseDto abrirOs(OrdemServicoRequestDto osRequestDto) {
        //var cliente = clienteService.buscarEntidadePorId(osRequestDto.clienteId());
        //var veiculo = veiculoService.buscarEntidadePorId(osRequestDto.veiculoId());
        //var ordemServico = new OrdemServico(cliente, veiculo, osRequestDto.quilometragemEntrada(), osRequestDto.reclamacaoCliente());
        //return mapearParaResponse(repository.save(ordemServico));
        return null;
    }

    @Transactional
    public OrdemServicoResponseDto iniciarDiagnostico(Long osId) {
        OrdemServico os = buscarOuFalhar(osId);
        validarStatusDiagnostico(os);
        os.setStatus(StatusOS.EM_DIAGNOSTICO);
        repository.save(os);
        return mapearParaResponse(os);
    }

    public OrdemServico buscarOuFalhar(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada."));
    }

    private void validarStatusDiagnostico(OrdemServico os) {
        if (os.getStatus() != StatusOS.RECEBIDA) {
            throw new IllegalStateException("Para iniciar um diagnóstico a OS deve estar com status RECEBIDA. Status atual da OS: " + os.getStatus());
        }
    }

    @Transactional
    public OrdemServicoResponseDto concluirDiagnostico(Long osId, String laudoTecnico) {
        OrdemServico os = buscarOuFalhar(osId);
        validarStatus(os, StatusOS.EM_DIAGNOSTICO);
        validarItensServico(os);
        os.setLaudoTecnico(laudoTecnico);
        os.setStatus(StatusOS.AGUARDANDO_ORCAMENTO);
        repository.save(os);
        return mapearParaResponse(os);
    }

    private void validarItensServico(OrdemServico os) {
        if (os.getItensServico() == null || os.getItensServico().isEmpty()) {
            throw new IllegalStateException("Para concluir o diagnóstico, a OS deve possuir ao menos um item de serviço.");
        }
    }

    private void validarStatus(OrdemServico os, StatusOS statusEsperado) {
        if (os.getStatus() != statusEsperado) {
            throw new IllegalStateException("Operação inválida para o status atual da OS: " + os.getStatus());
        }
    }

    @Transactional
    public LinkWhatsAppDto enviarOrcamento(Long osId) {
        OrdemServico os = buscarOuFalhar(osId);

        boolean temItemProdutoSemPreco = os.getItensProduto().stream()
                .anyMatch(item -> item.getValorUnitarioVenda().compareTo(BigDecimal.ZERO) <= 0);

        boolean temItemServicoSemValor = os.getItensServico().stream()
                .anyMatch(item -> item.getValorCobrado().compareTo(BigDecimal.ZERO) <= 0);

        if ( temItemProdutoSemPreco || temItemServicoSemValor ) {
            throw new IllegalStateException("Existem itens da OS sem valor R$.");
        }
        os.setStatus(StatusOS.AGUARDANDO_APROVACAO);
        repository.save(os);
        return getLinkWhatsAppDto(os);
    }

    private static LinkWhatsAppDto getLinkWhatsAppDto(OrdemServico os) {
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String valorFormatado = formatoMoeda.format(os.getValorTotalOS());
        String mensagem = String.format(
                "Olá %s, tudo bem? O orçamento do seu veículo está pronto! Total de %s. Podemos dar andamento no atendimento?",
                os.getCliente().getNome(),
                valorFormatado
        );
        String mensagemCodificada = URLEncoder.encode(mensagem, StandardCharsets.UTF_8);
        String telefoneLimpo = os.getCliente().getTelefone().replaceAll("[^0-9]", "");
        if (!telefoneLimpo.startsWith("55")) {
            telefoneLimpo = "55" + telefoneLimpo;
        }
        String urlFinal = "https://wa.me/" + telefoneLimpo + "?text=" + mensagemCodificada;
        return new LinkWhatsAppDto(urlFinal);
    }

    @Transactional
    public OrdemServicoResponseDto reprovarOrcamento(Long osId) {
        OrdemServico os = buscarOuFalhar(osId);
        validarStatus(os, StatusOS.AGUARDANDO_APROVACAO);
        os.setStatus(StatusOS.REPROVADA);
        os.setDataReprovacao(LocalDateTime.now());
        repository.save(os);
        return mapearParaResponse(os);
    }

    @Transactional
    public OrdemServicoResponseDto aprovarOrcamento(Long osId) {
        OrdemServico os = buscarOuFalhar(osId);
        validarStatus(os, StatusOS.AGUARDANDO_APROVACAO);
        movimentacaoEstoqueService.reservarItens(os);
        atualizarStatusPosReserva(os);
        repository.save(os);
        return mapearParaResponse(os);
    }

    private void atualizarStatusPosReserva(OrdemServico os) {
        boolean todosProdutosEstaoDisponiveis = os.getItensProduto().stream()
                .allMatch(item -> item.getProduto().getQuantidadeDisponivel().compareTo(BigDecimal.ZERO) >= 0);
        if (todosProdutosEstaoDisponiveis) {
            os.setStatus(StatusOS.AGUARDANDO_EXECUCAO);
            log.info("Ao aprovar a OS, foi constatado que todos os produtos estão disponíveis no estoque e pode ser iniciada a execução da OS.");
        } else {
            os.setStatus(StatusOS.AGUARDANDO_FORNECEDOR);
            log.info("Ao aprovar a OS, foi identificado que algum produto está sem saldo disponível no estoque sendo necessário realizar um pedido de compra.");
        }
    }

    @Transactional
    public OrdemServicoResponseDto verificarDisponibilidadePecas(Long osId) {
        OrdemServico os = buscarOuFalhar(osId);
        validarStatus(os, StatusOS.AGUARDANDO_FORNECEDOR);

        boolean todosProdutosEstaoDisponiveis = os.getItensProduto().stream()
                .allMatch(item -> item.getProduto().getQuantidadeDisponivel().compareTo(BigDecimal.ZERO) >= 0);

        if (todosProdutosEstaoDisponiveis) {
            os.setStatus(StatusOS.AGUARDANDO_EXECUCAO);
            repository.save(os);
            log.info("Todos os produtos estão disponíveis no estoque e pode ser iniciado a execução da OS.");
        } else {
            log.info("Ainda existem produtos sem saldo disponível no estoque, verificar com o fornecedor.");
        }

        return mapearParaResponse(os);
    }

    @Transactional
    public OrdemServicoResponseDto iniciarExecucaoOS(Long osId) {
        OrdemServico os = buscarOuFalhar(osId);
        validarStatus(os, StatusOS.AGUARDANDO_EXECUCAO);
        movimentacaoEstoqueService.consumirReservasParaExecucao(os);
        os.setStatus(StatusOS.EM_EXECUCAO);
        repository.save(os);
        return mapearParaResponse(os);
    }

    @Transactional
    public OrdemServicoResponseDto finalizarExecucaoOS(Long osId) {
        OrdemServico os = buscarOuFalhar(osId);
        validarStatus(os, StatusOS.EM_EXECUCAO);

        if (os.getItensServico() == null || os.getItensServico().isEmpty()) {
            throw new IllegalStateException("Falha de integridade: A OS chegou na finalização sem itens de serviço atrelados.");
        }
        boolean temServicoPendente = os.getItensServico().stream()
                .anyMatch(item -> item.getStatusItem() != StatusItemServico.CONCLUIDO);

        if (temServicoPendente) {
            throw new IllegalStateException("Não é possível finalizar a OS. Existem serviços pendentes ou em andamento.");
        }
        os.setStatus(StatusOS.FINALIZADA);
        repository.save(os);
        return mapearParaResponse(os);
    }

    @Transactional
    public OrdemServicoResponseDto entregarVeiculo(Long osId) {
        OrdemServico os = buscarOuFalhar(osId);
        validarStatus(os, StatusOS.FINALIZADA);
        //TODO - Faturamento - Nota de Servico
        os.setStatus(StatusOS.ENTREGUE);
        os.setDataSaida(LocalDateTime.now());
        repository.save(os);
        return mapearParaResponse(os);
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
        os.setValorTotalOS(totalProdutos.add(totalServicos).subtract(os.getValorDesconto()).max(BigDecimal.ZERO));

        repository.save(os);
    }

    public Page<OrdemServicoResponseDto> listarTodas(Pageable pageable) {
        Page<OrdemServico> ordens = repository.findAll(pageable);
        return ordens.map(this::mapearParaResponse);
    }

    public OrdemServicoResponseDto buscarPorId(Long id) {
        OrdemServico ordemServico = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada."));
        return mapearParaResponse(ordemServico);
    }

    public OrdemServicoPublicResponseDto buscarPorCodigoRastreio(String codigoRastreio) {
        OrdemServico ordemServico = repository.findByCodigoRastreio(codigoRastreio).orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada para o código de rastreio: " + codigoRastreio));
        return mapearParaPublicResponse(ordemServico);
    }

    @Transactional(readOnly = true)
    public OrdemServicoDetalhadaResponseDto buscarDetalhesPorCodigoRastreio(String codigoRastreio) {
        return repository.buscarPorCodigoRastreioComDetalhes(codigoRastreio)
                .map(os -> {
                    var servicos = os.getItensServico().stream()
                            .map(is -> new ItemServicoDetalheDto(
                                    is.getId(),
                                    is.getServico().getDescricao(),
                                    is.getMecanico() != null ? is.getMecanico().getNome() : "Não atribuído",
                                    is.getValorCobrado(),
                                    is.getStatusItem().name(),
                                    is.getDataInicio(),
                                    is.getDataFim()
                            )).toList();

                    var produtos = os.getItensProduto().stream()
                            .map(ip -> new ItemProdutoDetalheDto(
                                    ip.getId(),
                                    ip.getProduto().getNome(),
                                    ip.getQuantidade(),
                                    ip.getValorUnitarioVenda(),
                                    ip.getValorTotal()
                            )).toList();

                    return new OrdemServicoDetalhadaResponseDto(
                            os.getId(),
                            os.getCodigoRastreio(),
                            os.getCliente().getNome(),
                            os.getVeiculo().getPlaca(),
                            os.getReclamacaoCliente(),
                            os.getLaudoTecnico(),
                            os.getValorTotalServicos(),
                            os.getValorTotalProdutos(),
                            os.getValorTotalOS(),
                            os.getStatus().name(),
                            os.getDataEntrada(),
                            servicos,
                            produtos
                    );
                })
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ordem de Serviço não encontrada: " + codigoRastreio));
    }

    //TODO - Implemenar o uso da biblioteca MapStruct
    private OrdemServicoResponseDto mapearParaResponse(OrdemServico ordemServico) {
        return new OrdemServicoResponseDto(
                ordemServico.getId(),
                ordemServico.getCodigoRastreio(),
                ordemServico.getCliente().getId(),
                ordemServico.getCliente().getNome(),
                ordemServico.getVeiculo().getId(),
                ordemServico.getVeiculo().getPlaca(),
                ordemServico.getReclamacaoCliente(),
                ordemServico.getQuilometragemEntrada(),
                ordemServico.getLaudoTecnico(),
                ordemServico.getValorTotalServicos(),
                ordemServico.getValorTotalProdutos(),
                ordemServico.getValorDesconto(),
                ordemServico.getValorTotalOS(),
                ordemServico.getStatus().toString()
        );
    }
    //TODO - Implemenar o uso da biblioteca MapStruct
    private OrdemServicoPublicResponseDto mapearParaPublicResponse(OrdemServico ordemServico) {
        return new OrdemServicoPublicResponseDto(
                ordemServico.getId(),
                ordemServico.getCodigoRastreio(),
                ordemServico.getCliente().getNome(),
                ordemServico.getVeiculo().getPlaca(),
                ordemServico.getReclamacaoCliente(),
                ordemServico.getQuilometragemEntrada(),
                ordemServico.getLaudoTecnico(),
                ordemServico.getValorTotalServicos(),
                ordemServico.getValorTotalProdutos(),
                ordemServico.getValorDesconto(),
                ordemServico.getValorTotalOS(),
                ordemServico.getStatus().toString()
        );
    }
}