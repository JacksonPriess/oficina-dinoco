package com.dinoco.oficina.ordemservico.application.usecases.commands.concluir;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class ConcluirOrdemServicoHandler implements ConcluirOrdemServicoUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;

    public ConcluirOrdemServicoHandler(OrdemServicoCommandGateway ordemServicoCommandGateway) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
    }

    @Override
    public void executar(ConcluirOrdemServicoCommand command) {

        OrdemServico ordemServico = ordemServicoCommandGateway.buscarParaAlteracao(command.osId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada."));

        ordemServico.concluir();

        ordemServicoCommandGateway.salvar(ordemServico);

        Duration duracao = Duration.between(ordemServico.getDataEntrada(), ordemServico.getDataSaida());
        log.info("evento=os_ciclo_completo_concluido osId={} duracaoMs={}", ordemServico.getId(), duracao.toMillis());
    }
}