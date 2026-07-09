package com.dinoco.oficina.metrica.infrastructure.web;


import com.dinoco.oficina.metrica.adapters.controllers.MetricaControllerClean;
import com.dinoco.oficina.metrica.application.usecase.queries.buscarmediaexecucao.BuscarMediaExecucaoServicosOutput;
import com.dinoco.oficina.metrica.application.usecase.queries.buscarmediaexecucao.BuscarMediaExecucaoServicosQuery;
import com.dinoco.oficina.metrica.infrastructure.web.dto.RelatorioMetricasDTO;
import com.dinoco.oficina.metrica.infrastructure.web.dto.RelatorioMetricasRequestDTO;
import com.dinoco.oficina.metrica.infrastructure.web.mapper.MetricaWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Métricas", description = "Relatórios")
@RestController
@RequestMapping("api/metricas")
public class MetricaSpringWebController {

    private final MetricaControllerClean controllerClean;
    private final MetricaWebMapper mapper;

    public MetricaSpringWebController(MetricaControllerClean controllerClean, MetricaWebMapper mapper) {
        this.controllerClean = controllerClean;
        this.mapper = mapper;
    }

    @Operation(summary = "Média de execução dos serviços")
    @GetMapping("/tempo-execucao")
    public ResponseEntity<RelatorioMetricasDTO> getMetricas(@Valid RelatorioMetricasRequestDTO request) {
        BuscarMediaExecucaoServicosQuery input = mapper.toInput(request);
        BuscarMediaExecucaoServicosOutput buscarMediaExecucaoServicosOutput = controllerClean.buscarMediaExecucaoServicos(input);
        RelatorioMetricasDTO response = mapper.toRelatorioMetricasDTO(buscarMediaExecucaoServicosOutput);
        return ResponseEntity.ok(response);
    }
}