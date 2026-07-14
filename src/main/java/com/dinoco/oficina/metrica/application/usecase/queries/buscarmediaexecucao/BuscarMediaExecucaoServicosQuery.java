package com.dinoco.oficina.metrica.application.usecase.queries.buscarmediaexecucao;

import java.time.LocalDate;

public record BuscarMediaExecucaoServicosQuery(
        LocalDate dataInicio,
        LocalDate dataFinal)
{}