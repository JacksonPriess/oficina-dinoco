package com.dinoco.oficina.funcionario.application.usecases.queries.buscarporid;

import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.funcionario.application.gateways.FuncionarioQueryGateway;

public class BuscarFuncionarioPorIdHandler implements BuscarFuncionarioPorIdUseCase {

    private final FuncionarioQueryGateway funcionarioQueryGateway;

    public BuscarFuncionarioPorIdHandler(FuncionarioQueryGateway funcionarioQueryGateway) {
        this.funcionarioQueryGateway = funcionarioQueryGateway;
    }

    @Override
    public BuscarFuncionarioPorIdOutput executar(BuscarFuncionarioPorIdQuery query) {
        return funcionarioQueryGateway.buscarDetalhesPorId(query.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário não encontrado com ID: " + query.id()));
    }
}

