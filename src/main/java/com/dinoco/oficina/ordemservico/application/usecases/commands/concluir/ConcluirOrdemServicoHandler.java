package com.dinoco.oficina.ordemservico.application.usecases.commands.concluir;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;

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
    }
}

