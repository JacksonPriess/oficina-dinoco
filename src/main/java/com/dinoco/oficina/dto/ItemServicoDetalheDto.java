package com.dinoco.oficina.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ItemServicoDetalheDto(
        Long id,
        String descricao,
        String mecanico,
        BigDecimal valorCobrado,
        String status,
        LocalDateTime dataInicio,
        LocalDateTime dataFim
) {}