package com.dinoco.oficina.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

// DTO para Adicionar Produto na OS
public record ItemProdutoAdicionarDto(
        @NotNull
        Long produtoId,

        @NotNull
        BigDecimal quantidade,

        @NotNull
        BigDecimal valorUnitarioVenda
) {}
