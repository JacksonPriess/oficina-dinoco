package com.dinoco.oficina.ordemservico.application.usecases.commands.iniciardiagnostico;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.domain.exceptions.RecursoNaoEncontradoException;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;

public class IniciarDiagnosticoHandler implements IniciarDiagnosticoUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;

    public IniciarDiagnosticoHandler(OrdemServicoCommandGateway ordemServicoCommandGateway) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
    }

    @Override
    public void executar(IniciarDiagnosticoCommand command) {
        OrdemServico os = ordemServicoCommandGateway.buscarParaAlteracao(command.osId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada."));
        os.iniciarDiagnostico();
        ordemServicoCommandGateway.salvar(os);
    }

}