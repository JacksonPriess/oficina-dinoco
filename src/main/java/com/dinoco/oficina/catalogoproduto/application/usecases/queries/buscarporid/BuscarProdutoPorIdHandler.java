package com.dinoco.oficina.catalogoproduto.application.usecases.queries.buscarporid;

import com.dinoco.oficina.catalogoproduto.application.gateways.ProdutoQueryGateway;

public class BuscarProdutoPorIdHandler implements BuscarProdutoPorIdUseCase {

    private final ProdutoQueryGateway produtoQueryGateway;

    public BuscarProdutoPorIdHandler(ProdutoQueryGateway produtoQueryGateway) {
        this.produtoQueryGateway = produtoQueryGateway;
    }

    @Override
    public BuscarProdutoPorIdOutput executar(BuscarProdutoPorIdQuery query) {
        return produtoQueryGateway.buscarDetalhesPorId(query.id())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + query.id()));
    }
}