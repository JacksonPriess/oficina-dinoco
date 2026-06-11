package com.dinoco.oficina.estoque.infrastructure.web.dto;

import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record SaldoEstoqueRequestDto(

    @PositiveOrZero
    BigDecimal quantidade

) {}
