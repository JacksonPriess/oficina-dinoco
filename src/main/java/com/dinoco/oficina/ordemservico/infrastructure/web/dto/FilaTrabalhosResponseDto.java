package com.dinoco.oficina.ordemservico.infrastructure.web.dto;

import java.time.LocalDateTime;
import java.util.List;

public record FilaTrabalhosResponseDto(
        LocalDateTime dataConsulta,
        List<FilaTrabalhosDetalhesResponseDto> ordensServico
) {

}