package com.dinoco.oficina.metrica.infrastructure.web.dto;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public record RelatorioMetricasRequestDTO(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dataInicio,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dataFinal
) {}
