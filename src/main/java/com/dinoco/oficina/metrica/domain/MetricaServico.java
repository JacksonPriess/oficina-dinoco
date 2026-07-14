package com.dinoco.oficina.metrica.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Este record pertence à camada Domain
public record MetricaServico(
        Long servicoId,
        String descricao,
        Long quantidade,
        BigDecimal mediaMinutos,
        Integer tempoPadraoMinutos
) {
    public MetricaServico {
        if (mediaMinutos == null) {
            mediaMinutos = BigDecimal.ZERO;
        } else {
            mediaMinutos = mediaMinutos.setScale(2, RoundingMode.HALF_UP);
        }
    }

    public BigDecimal calcularDesvioAbsoluto() {
        return mediaMinutos.subtract(BigDecimal.valueOf(tempoPadraoMinutos))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularPercentualDiferenca() {
        if (tempoPadraoMinutos <= 0) {
            return BigDecimal.ZERO;
        }
        return calcularDesvioAbsoluto()
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(tempoPadraoMinutos), 2, RoundingMode.HALF_UP);
    }
}