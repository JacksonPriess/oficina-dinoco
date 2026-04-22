package com.dinoco.oficina.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record ItemServicoAlterarDto(
        @NotNull
        @PositiveOrZero
        BigDecimal valorCobrado,
        Long mecanicoId
) {}