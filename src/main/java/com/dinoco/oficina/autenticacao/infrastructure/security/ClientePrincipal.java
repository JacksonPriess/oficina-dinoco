package com.dinoco.oficina.autenticacao.infrastructure.security;

/**
 * Representará o cliente autenticado dentro do Spring Security.
 */
public record ClientePrincipal(Long clienteId) {
}