package com.dinoco.oficina.autenticacao.infrastructure.gateways;

import com.dinoco.oficina.autenticacao.application.gateways.PasswordEncoderGateway;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderGatewayImpl implements PasswordEncoderGateway {

    private final PasswordEncoder passwordEncoder;

    public PasswordEncoderGatewayImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String criptografar(String senhaPura) {
        return passwordEncoder.encode(senhaPura);
    }
}
