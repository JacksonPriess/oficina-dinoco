package com.dinoco.oficina.autenticacao.application.gateways;

public interface PasswordEncoderGateway {
    String criptografar(String senhaPura);
}
