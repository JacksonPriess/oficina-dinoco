package com.dinoco.oficina.veiculo.application.usecases.queries.buscarporid;

import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.veiculo.application.gateways.VeiculoQueryGateway;

public class BuscarVeiculoPorIdHandler implements BuscarVeiculoPorIdUseCase {

    private final VeiculoQueryGateway veiculoQueryGateway;

    public BuscarVeiculoPorIdHandler(VeiculoQueryGateway veiculoQueryGateway) {
        this.veiculoQueryGateway = veiculoQueryGateway;
    }

    @Override
    public BuscarVeiculoPorIdOutput executar(BuscarVeiculoPorIdQuery query) {
        return veiculoQueryGateway.buscarDetalhesPorId(query.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veiculo não encontrado com ID: " + query.id()));
    }
}