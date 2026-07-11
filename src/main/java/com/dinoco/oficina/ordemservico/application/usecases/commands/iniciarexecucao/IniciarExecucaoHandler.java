package com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucao;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoEventPublisher;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import com.dinoco.oficina.shared.events.ExecucaoIniciadaEvent;

public class IniciarExecucaoHandler implements IniciarExecucaoUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;
    private final OrdemServicoEventPublisher ordemServicoEventPublisher;

    public IniciarExecucaoHandler(OrdemServicoCommandGateway ordemServicoCommandGateway, OrdemServicoEventPublisher ordemServicoEventPublisher) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
        this.ordemServicoEventPublisher = ordemServicoEventPublisher;
    }

    @Override
    public void executar(IniciarExecucaoCommand command) {

        OrdemServico ordemServico = ordemServicoCommandGateway.buscarParaAlteracao(command.osId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada."));

        ordemServico.iniciarExecucao();

        ordemServicoCommandGateway.salvar(ordemServico);

        ordemServicoEventPublisher.publicarExecucaoIniciada(new ExecucaoIniciadaEvent(ordemServico.getId(), ordemServico.getItensProduto()));
    }
}

