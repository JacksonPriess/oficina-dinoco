package com.dinoco.oficina.ordemservico.application.usecases.commands.finalizarexecucao;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class FinalizarExecucaoHandler implements FinalizarExecucaoUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;

    public FinalizarExecucaoHandler(OrdemServicoCommandGateway ordemServicoCommandGateway) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
    }

    @Override
    public void executar(FinalizarExecucaoCommand command) {

        OrdemServico ordemServico = ordemServicoCommandGateway.buscarParaAlteracao(command.osId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada."));

        ordemServico.finalizarExecucao();

        ordemServicoCommandGateway.salvar(ordemServico);

        Duration duracao = Duration.between(ordemServico.getDataInicioExecucao(), ordemServico.getDataFinalExecucao());
        log.info("evento=os_execucao_concluida osId={} duracaoMs={}", ordemServico.getId(), duracao.toMillis());
    }
}