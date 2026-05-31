package com.dinoco.oficina.catalogoproduto.application.usecases.queries.buscarportermo;

import java.util.List;

public interface BuscarProdutoPorTermoUseCase {
    List<BuscarProdutoPorTermoOutput> executar(BuscarProdutoPorTermoQuery query);
}