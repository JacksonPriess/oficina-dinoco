package com.dinoco.oficina.metrica.infrastructure.web.dto;

import java.math.BigDecimal;

public record DetalheMetricaServicoDTO(
        Long codigoServico,
        String descricao,
        Long quantidadeExecutada,
        BigDecimal mediaMinutos,
        Integer tempoPadraoMinutos,
        BigDecimal desvioAbsolutoMinutos,
        BigDecimal percentualDiferenca
) {}


