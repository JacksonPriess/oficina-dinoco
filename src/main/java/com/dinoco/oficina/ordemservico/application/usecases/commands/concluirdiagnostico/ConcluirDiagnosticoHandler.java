package com.dinoco.oficina.ordemservico.application.usecases.commands.concluirdiagnostico;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;

public class ConcluirDiagnosticoHandler implements ConcluirDiagnosticoUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;

    public ConcluirDiagnosticoHandler(OrdemServicoCommandGateway ordemServicoCommandGateway) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
    }

    @Override
    public void executar(ConcluirDiagnosticoCommand command) {
        OrdemServico os = ordemServicoCommandGateway.buscarParaAlteracao(command.osId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada."));
        os.concluirDiagnostico(command.laudo());
        ordemServicoCommandGateway.salvar(os);
    }
}