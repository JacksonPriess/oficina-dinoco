package com.dinoco.oficina.estoque.application.usecases.queries.buscarporid;

import java.math.BigDecimal;

public record BuscarEstoquePorIdOuput(
        Long id,
        Long produtoId,
        BigDecimal quantidadeReal,
        BigDecimal quantidadeReservada
)
{}