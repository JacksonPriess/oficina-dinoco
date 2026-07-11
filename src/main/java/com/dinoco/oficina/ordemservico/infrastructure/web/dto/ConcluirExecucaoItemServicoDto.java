package com.dinoco.oficina.ordemservico.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ConcluirExecucaoItemServicoDto(

        @Schema(description = "Data e hora manual da conclusão. Se não enviada, assume o momento atual.")
        LocalDateTime dataHoraFim
) {}