package com.dinoco.oficina.service;

import com.dinoco.oficina.dto.*;
import com.dinoco.oficina.entity.*;
import com.dinoco.oficina.enums.StatusItemServico;
import com.dinoco.oficina.enums.StatusOS;
import com.dinoco.oficina.repository.*;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class OrdemServicoService {

    private final OrdemServicoRepository osRepository;
    private final ClienteService clienteService;
    private final VeiculoService veiculoService;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    @Transactional
    public OrdemServicoResponseDto abrirOs(OrdemServicoRequestDto osRequestDto) {
        //TODO - Criar tratamento para não permitir ter mais de uma OS "aberta" para o mesmo veículo.
        var cliente = clienteService.buscarEntidadePorId(osRequestDto.clienteId());
        var veiculo = veiculoService.buscarEntidadePorId(osRequestDto.veiculoId());
        var ordemServico = new OrdemServico(cliente, veiculo, osRequestDto.quilometragemEntrada(), osRequestDto.reclamacaoCliente());
        return mapearParaResponse(osRepository.save(ordemServico));
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
        osRepository.save(os);

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
        movimentacaoEstoqueService.reservarItens(os);
        atualizarStatusPosReserva(os);
        osRepository.save(os);
    }

    private void atualizarStatusPosReserva(OrdemServico os) {
        boolean temTudoNoEstoque = os.getItensProduto().stream()
                .allMatch(item -> item.getProduto().getQuantidadeDisponivel().compareTo(BigDecimal.ZERO) >= 0);

        os.setStatus(temTudoNoEstoque ? StatusOS.AGUARDANDO_EXECUCAO : StatusOS.AGUARDANDO_FORNECEDOR);
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
        movimentacaoEstoqueService.consumirReservasParaExecucao(os);
        os.setStatus(StatusOS.EM_EXECUCAO);
        osRepository.save(os);
    }

    @Transactional
    public void finalizarExecucaoOS(Long osId) {
        OrdemServico os = buscarOuFalhar(osId);
        validarStatus(os, StatusOS.EM_EXECUCAO);

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
        os.setValorTotalOS(totalProdutos.add(totalServicos).subtract(os.getValorDesconto()).max(BigDecimal.ZERO));

        osRepository.save(os);
    }

    public OrdemServico buscarOuFalhar(Long id) {
        return osRepository.findById(id).orElseThrow(() -> new RuntimeException("OS não encontrada"));
    }

    public OrdemServicoResponseDto buscarPorId(Long id) {
        OrdemServico ordemServico = osRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("OS não encontrada."));
        return mapearParaResponse(ordemServico);
    }

    public OrdemServicoResponseDto buscarPorCodigoRastreio(String codigoRastreio) {
        OrdemServico ordemServico = osRepository.findByCodigoRastreio(codigoRastreio).orElseThrow(() -> new IllegalArgumentException("OS não encontrada para o código de rastreio: " + codigoRastreio));
        return mapearParaResponse(ordemServico);
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