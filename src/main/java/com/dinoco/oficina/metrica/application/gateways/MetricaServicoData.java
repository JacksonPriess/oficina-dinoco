package com.dinoco.oficina.metrica.application.gateways;

import java.math.BigDecimal;

public record MetricaServicoData(
        Long servicoId,
        String descricao,
        Long quantidade,
        BigDecimal mediaMinutos,
        Integer tempoPadraoMinutos
) {}
