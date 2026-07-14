package com.dinoco.oficina.ordemservico.application.usecases.commands.concluirexecucaoitemservico;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;

public class ConcluirExecucaoItemServicoHandler implements ConcluirExecucaoItemServicoUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;

    public ConcluirExecucaoItemServicoHandler(OrdemServicoCommandGateway ordemServicoCommandGateway) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
    }

    @Override
    public void executar(ConcluirExecucaoItemServicoCommand command) {
        OrdemServico ordemServico = ordemServicoCommandGateway.buscarParaAlteracao(command.osId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada."));

        ordemServico.concluirExecucaoItemServico(command.itemId(), command.dataHoraFim());
        ordemServicoCommandGateway.salvar(ordemServico);
    }
}