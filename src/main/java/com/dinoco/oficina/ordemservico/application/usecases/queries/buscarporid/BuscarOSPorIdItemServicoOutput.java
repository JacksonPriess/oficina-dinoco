package com.dinoco.oficina.ordemservico.application.usecases.queries.buscarporid;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BuscarOSPorIdItemServicoOutput(
        Long id,
        Long servicoId,
        Long mecanicoId,
        BigDecimal valorCobrado,
        String statusItem,
        LocalDateTime dataInicio,
        LocalDateTime dataFim
) {}
