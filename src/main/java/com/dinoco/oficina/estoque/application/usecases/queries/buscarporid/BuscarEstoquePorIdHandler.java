package com.dinoco.oficina.estoque.application.usecases.queries.buscarporid;

import com.dinoco.oficina.estoque.application.gateways.EstoqueQueryGateway;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;

public class BuscarEstoquePorIdHandler implements BuscarEstoquePorIdUseCase {

    private final EstoqueQueryGateway estoqueQueryGateway;

    public BuscarEstoquePorIdHandler(EstoqueQueryGateway estoqueQueryGateway) {
        this.estoqueQueryGateway = estoqueQueryGateway;
    }

    @Override
    public BuscarEstoquePorIdOuput executar(BuscarEstoquePorIdQuery query) {
        return estoqueQueryGateway.buscarPorProdutoId(query.produtoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estoque não encontrado para o ID do produto: " + query.produtoId()));
    }
}
