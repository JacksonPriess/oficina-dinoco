package com.dinoco.oficina.ordemservico.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BuscarOSPorIdItemServicoResponseDto(
        Long id,
        Long servicoId,
        Long mecanicoId,
        BigDecimal valorCobrado,
        String statusItem,
        LocalDateTime dataInicio,
        LocalDateTime dataFim
) {}
