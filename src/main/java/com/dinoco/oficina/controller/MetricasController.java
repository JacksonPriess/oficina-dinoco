package com.dinoco.oficina.controller;

import com.dinoco.oficina.dto.RelatorioMetricasDTO;
import com.dinoco.oficina.service.MetricasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Tag(name = "10. Métricas", description = "Relatórios")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/metricas")
public class MetricasController {

    private final MetricasService metricasService;

    @Operation(summary = "Média de execução dos serviços")
    @GetMapping("/tempo-execucao")
    public ResponseEntity<RelatorioMetricasDTO> getMetricas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        RelatorioMetricasDTO relatorio = metricasService.gerarRelatorioTempoExecucao(inicio, fim);
        return ResponseEntity.ok(relatorio);
    }
}