package com.dinoco.oficina.ordemservico.application.usecases.commands.reprovar;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.domain.exceptions.RecursoNaoEncontradoException;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;

public class ReprovarOrcamentoHandler implements ReprovarOrcamentoUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;

    public ReprovarOrcamentoHandler(OrdemServicoCommandGateway ordemServicoCommandGateway) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
    }

    @Override
    public void executar(ReprovarOrcamentoCommand command) {
        OrdemServico ordemServico = ordemServicoCommandGateway.buscarParaAlteracao(command.osId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada."));
        ordemServico.reprovarOrcamento();
        ordemServicoCommandGateway.salvar(ordemServico);
    }
}

