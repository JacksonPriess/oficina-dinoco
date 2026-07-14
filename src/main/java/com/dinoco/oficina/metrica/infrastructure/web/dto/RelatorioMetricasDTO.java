package com.dinoco.oficina.metrica.infrastructure.web.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record RelatorioMetricasDTO(
        LocalDateTime dataConsulta,
        LocalDate dataInicioFiltro,
        LocalDate dataFimFiltro,
        List<DetalheMetricaServicoDTO> detalhes
) {}
