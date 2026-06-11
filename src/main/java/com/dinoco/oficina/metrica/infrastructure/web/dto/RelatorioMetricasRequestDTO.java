package com.dinoco.oficina.metrica.infrastructure.web.dto;

import java.time.LocalDate;

public record RelatorioMetricasRequestDTO(
        LocalDate dataInicio,
        LocalDate dataFinal
) {}
