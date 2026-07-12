package com.dinoco.oficina.ordemservico.application.usecases.queries.listarfilatrabalho;

import java.time.LocalDateTime;
import java.util.List;

public record ListarFilaTrabalhoOutput(
        LocalDateTime dataConsulta,
        List<ListarFilaTrabalhoDetalhesOutput> ordensServico
) {}
