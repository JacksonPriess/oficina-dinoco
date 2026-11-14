package com.dinoco.oficina.ordemservico.application.usecases.queries.buscarporid;

import java.math.BigDecimal;

public record BuscarOSPorIdItemProdutoOutput(
        Long id,
        Long produtoId,
        BigDecimal quantidade,
        BigDecimal valorUnitarioVenda,
        BigDecimal valorTotal
) {}
