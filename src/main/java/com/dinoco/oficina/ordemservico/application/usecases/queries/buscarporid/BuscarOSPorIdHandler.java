package com.dinoco.oficina.ordemservico.application.usecases.queries.buscarporid;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoQueryGateway;
import com.dinoco.oficina.ordemservico.domain.exceptions.RecursoNaoEncontradoException;

public class BuscarOSPorIdHandler implements BuscarOSPorIdUseCase {

    private final OrdemServicoQueryGateway ordemServicoQueryGateway;

    public BuscarOSPorIdHandler(OrdemServicoQueryGateway ordemServicoQueryGateway) {
        this.ordemServicoQueryGateway = ordemServicoQueryGateway;
    }

    @Override
    public BuscarOSPorIdOuput executar(BuscarOSPorIdQuery query) {

        return ordemServicoQueryGateway.buscarPorId(query.osId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ordem de Serviço não encontrada para o ID: " + query.osId()));

    }
}
