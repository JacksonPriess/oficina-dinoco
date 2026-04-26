package com.dinoco.oficina.dto;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ItemOSServicoAlterarDto(

        @Positive
        BigDecimal valorCobrado,
        Long mecanicoId
) {}