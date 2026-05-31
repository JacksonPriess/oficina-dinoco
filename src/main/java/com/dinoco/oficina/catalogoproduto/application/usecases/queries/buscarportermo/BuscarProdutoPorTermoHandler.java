package com.dinoco.oficina.catalogoproduto.application.usecases.queries.buscarportermo;

import com.dinoco.oficina.catalogoproduto.application.gateways.ProdutoQueryGateway;
import java.util.List;

public class BuscarProdutoPorTermoHandler implements BuscarProdutoPorTermoUseCase {

    private final ProdutoQueryGateway produtoQueryGateway;

    public BuscarProdutoPorTermoHandler(ProdutoQueryGateway produtoQueryGateway) {
        this.produtoQueryGateway = produtoQueryGateway;
    }

    @Override
    public List<BuscarProdutoPorTermoOutput> executar(BuscarProdutoPorTermoQuery query) {
        return produtoQueryGateway.buscarDetalhesPorTermo(query.termo());

    }
}