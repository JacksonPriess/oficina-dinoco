package com.dinoco.oficina.enums;

public enum TipoMovimentacao {
    ENTRADA_FORNECEDOR, // Aumenta Físico
    RESERVA_OS,         // Aumenta Reservado
    BAIXA_EXECUCAO_OS,  // Diminui Físico e Diminui Reservado
    ESTORNO_RESERVA,    // Futura implementacao
    AJUSTE_MANUAL       // Balanço de estoque (quando o dono conta e vê que sumiu peça)
}
