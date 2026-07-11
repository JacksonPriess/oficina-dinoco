package com.dinoco.oficina.ordemservico.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record ItemOSProdutoAlterarDto(

        @NotNull
        @Positive(message = "A quantidade deve ser maior que zero")
        BigDecimal quantidade,

        @NotNull
        @PositiveOrZero(message = "O valor não pode ser negativo")
        BigDecimal precoVenda
) {}