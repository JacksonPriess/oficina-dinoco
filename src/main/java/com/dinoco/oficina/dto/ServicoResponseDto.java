package com.dinoco.oficina.dto;

import java.math.BigDecimal;

public record ServicoResponseDto(
        Long id,
        String descricao,
        BigDecimal precoPadrao,
        Integer tempoEstimadoMinutos,
        Boolean ativo
) {}