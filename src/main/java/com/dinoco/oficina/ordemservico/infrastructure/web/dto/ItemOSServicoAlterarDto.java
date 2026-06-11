package com.dinoco.oficina.ordemservico.infrastructure.web.dto;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ItemOSServicoAlterarDto(
        @Positive
        BigDecimal valorCobrado,
        Long mecanicoId
) {}