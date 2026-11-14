package com.dinoco.oficina.ordemservico.infrastructure.web.dto;

import java.math.BigDecimal;

public record BuscarOSPorIdItemProdutoResponseDto(
        Long id,
        Long produtoId,
        BigDecimal quantidade,
        BigDecimal valorUnitarioVenda,
        BigDecimal valorTotal
) {}
