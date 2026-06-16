package com.dinoco.oficina.estoque.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record SaldoEstoqueRequestDto(
    @PositiveOrZero
    BigDecimal quantidade,

    @NotNull(message = "A versão é obrigatória.")
    Long versao,

    String observacao
) {}
