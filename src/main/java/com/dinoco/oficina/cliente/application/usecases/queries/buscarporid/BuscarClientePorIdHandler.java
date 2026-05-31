package com.dinoco.oficina.cliente.application.usecases.queries.buscarporid;

import com.dinoco.oficina.cliente.application.gateways.ClienteQueryGateway;

public class BuscarClientePorIdHandler implements BuscarClientePorIdUseCase {

    private final ClienteQueryGateway clienteQueryGateway;

    public BuscarClientePorIdHandler(ClienteQueryGateway clienteQueryGateway) {
        this.clienteQueryGateway = clienteQueryGateway;
    }

    @Override
    public BuscarClientePorIdOutput executar(BuscarClientePorIdQuery query) {

        return clienteQueryGateway.buscarDetalhesPorId(query.id())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID: " + query.id()));
    }
}