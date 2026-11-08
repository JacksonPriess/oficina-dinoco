package com.dinoco.oficina.ordemservico.application.usecases.commands.concluir;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.application.saga.OrquestradorPagamento;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class ConcluirOrdemServicoHandler implements ConcluirOrdemServicoUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;

    private final OrquestradorPagamento orquestradorPagamento;

    public ConcluirOrdemServicoHandler(OrdemServicoCommandGateway ordemServicoCommandGateway,
                                        OrquestradorPagamento orquestradorPagamento) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
        this.orquestradorPagamento = orquestradorPagamento;
    }

    @Override
    public void executar(ConcluirOrdemServicoCommand command) {

        OrdemServico ordemServico = ordemServicoCommandGateway.bloquearParaAlteracao(command.osId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada."));

        ordemServico.concluir();
        orquestradorPagamento.autorizarEntrega(ordemServico.getId(), ordemServico.getValorTotalOS());

        ordemServicoCommandGateway.salvar(ordemServico);

        Duration duracao = Duration.between(ordemServico.getDataEntrada(), ordemServico.getDataSaida());
        log.info("evento=os_ciclo_completo_concluido osId={} duracaoMs={}", ordemServico.getId(), duracao.toMillis());
    }
}