package com.dinoco.oficina.autenticacao.infrastructure.security;

public record TokenAutenticado(
        String subject,
        String tipo
) {
    public boolean isCliente() {
        return "CLIENTE".equalsIgnoreCase(tipo);
    }
}