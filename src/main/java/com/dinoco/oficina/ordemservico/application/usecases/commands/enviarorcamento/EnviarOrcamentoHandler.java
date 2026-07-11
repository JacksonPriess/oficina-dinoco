package com.dinoco.oficina.ordemservico.application.usecases.commands.enviarorcamento;

import com.dinoco.oficina.ordemservico.application.gateways.ClienteContatoGateway;
import com.dinoco.oficina.ordemservico.application.gateways.ClienteContatoOutput;
import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;

public class EnviarOrcamentoHandler implements EnviarOrcamentoUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;
    private final ClienteContatoGateway clienteContatoGateway;

    public EnviarOrcamentoHandler(OrdemServicoCommandGateway ordemServicoCommandGateway, ClienteContatoGateway clienteContatoGateway) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
        this.clienteContatoGateway = clienteContatoGateway;
    }

    @Override
    public EnviarOrcamentoOutput executar(EnviarOrcamentoCommand command) {

        OrdemServico ordemServico = ordemServicoCommandGateway.buscarParaAlteracao(command.osId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada."));

        ordemServico.enviarOrcamento();

        ordemServicoCommandGateway.salvar(ordemServico);

        ClienteContatoOutput contatoCliente = clienteContatoGateway.buscarContato(ordemServico.getClienteId());
        String urlWhatsApp = gerarLinkWhatsApp(contatoCliente.nome(), contatoCliente.telefone(), ordemServico.getValorTotalOS());
        return new EnviarOrcamentoOutput(urlWhatsApp);
    }

    private String gerarLinkWhatsApp(String nomeCliente, String telefone, java.math.BigDecimal valorTotal) {
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String valorFormatado = formatoMoeda.format(valorTotal);

        String mensagem = String.format(
                "Olá %s, tudo bem? O orçamento do seu veículo está pronto! Total de %s. Podemos dar andamento no atendimento?",
                nomeCliente,
                valorFormatado
        );

        String mensagemCodificada = URLEncoder.encode(mensagem, StandardCharsets.UTF_8);

        String telefoneLimpo = telefone.replaceAll("[^0-9]", "");
        if (!telefoneLimpo.startsWith("55")) {
            telefoneLimpo = "55" + telefoneLimpo;
        }

        return "https://wa.me/" + telefoneLimpo + "?text=" + mensagemCodificada;
    }
}
