package com.dinoco.oficina.catalogoproduto.application.gateways;

import com.dinoco.oficina.catalogoproduto.application.usecases.queries.buscarportermo.BuscarProdutoPorTermoOutput;
import com.dinoco.oficina.catalogoproduto.application.usecases.queries.buscarporid.BuscarProdutoPorIdOutput;

import java.util.List;
import java.util.Optional;

public interface ProdutoQueryGateway {

    boolean existePorNome(String nome);
    Optional<BuscarProdutoPorIdOutput> buscarDetalhesPorId(Long id);
    List<BuscarProdutoPorTermoOutput> buscarDetalhesPorTermo(String termo);
}