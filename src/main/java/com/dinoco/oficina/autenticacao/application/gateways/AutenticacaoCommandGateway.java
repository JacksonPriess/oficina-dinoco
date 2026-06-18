package com.dinoco.oficina.autenticacao.application.gateways;

public interface AutenticacaoCommandGateway {
    boolean autenticar(String username, String password);
}
