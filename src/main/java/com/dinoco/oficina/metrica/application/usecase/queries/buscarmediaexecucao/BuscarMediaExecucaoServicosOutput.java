package com.dinoco.oficina.metrica.application.usecase.queries.buscarmediaexecucao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record BuscarMediaExecucaoServicosOutput(
        LocalDateTime dataConsulta,
        LocalDate dataInicioFiltro,
        LocalDate dataFimFiltro,
        List<BuscarMediaExecucaoServicosDetalhesOutput> detalhes)
{}