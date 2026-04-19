package com.dinoco.oficina.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record ItemServicoAdicionarDto(
        @NotNull
        Long servicoId,

        Long mecanicoId,

        @NotNull
        @PositiveOrZero
        BigDecimal valorCobrado
) {}