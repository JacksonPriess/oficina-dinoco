package com.dinoco.oficina.estoque.application.gateways;

import com.dinoco.oficina.estoque.application.usecases.queries.buscarporid.BuscarEstoquePorIdOuput;
import java.util.Optional;

public interface EstoqueQueryGateway {
    Optional<BuscarEstoquePorIdOuput> buscarPorProdutoId(Long id);
}