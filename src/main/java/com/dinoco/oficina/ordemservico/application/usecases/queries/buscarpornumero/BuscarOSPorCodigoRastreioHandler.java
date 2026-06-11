package com.dinoco.oficina.ordemservico.application.usecases.queries.buscarpornumero;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoQueryGateway;
import com.dinoco.oficina.ordemservico.domain.exceptions.RecursoNaoEncontradoException;

public class BuscarOSPorCodigoRastreioHandler implements BuscarOSPorCodigoRastreioUseCase {

    private final OrdemServicoQueryGateway ordemServicoQueryGateway;

    public BuscarOSPorCodigoRastreioHandler(OrdemServicoQueryGateway ordemServicoQueryGateway) {
        this.ordemServicoQueryGateway = ordemServicoQueryGateway;
    }

    @Override
    public BuscarOSPorCodigoRastreioOuput executar(BuscarOSPorCodigoRastreioQuery query) {

        return ordemServicoQueryGateway.buscarPorCodigoRastreio(query.codigoRastreio())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ordem de Serviço não encontrada para o código de rastreio: " + query.codigoRastreio()));

    }
}
