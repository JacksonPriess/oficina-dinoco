package com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucaoitemservico;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;

public class IniciarExecucaoItemServicoHandler implements IniciarExecucaoItemServicoUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;

    public IniciarExecucaoItemServicoHandler(OrdemServicoCommandGateway ordemServicoCommandGateway) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
    }

    @Override
    public void executar(IniciarExecucaoItemServicoCommand command) {
        OrdemServico ordemServico = ordemServicoCommandGateway.buscarParaAlteracao(command.osId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada."));

        ordemServico.iniciarExecucaoItemServico(command.itemId());
        ordemServicoCommandGateway.salvar(ordemServico);
    }
}