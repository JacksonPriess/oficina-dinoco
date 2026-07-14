package com.dinoco.oficina.ordemservico.application.usecases.commands.finalizarexecucao;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;

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
    }
}

