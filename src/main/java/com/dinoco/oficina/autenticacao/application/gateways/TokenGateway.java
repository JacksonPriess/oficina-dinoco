package com.dinoco.oficina.autenticacao.application.gateways;

public interface TokenGateway {
    String gerarToken(String username);
}
