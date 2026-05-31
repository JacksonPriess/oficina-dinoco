package com.dinoco.oficina.aaestoque.domain;

public enum TipoMovimentacao {
    ENTRADA,
    RESERVA_OS,
    BAIXA_EXECUCAO_OS, // Diminuir quantidade física e reservada
    AJUSTE_ENTRADA,
    AJUSTE_SAIDA

}
