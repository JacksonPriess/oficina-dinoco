package com.dinoco.oficina.ordemservico.infrastructure.web.dto;

import java.math.BigDecimal;

public record PecaPendenteDto(
        Long produtoId,
        BigDecimal quantidadeFaltante
) {}