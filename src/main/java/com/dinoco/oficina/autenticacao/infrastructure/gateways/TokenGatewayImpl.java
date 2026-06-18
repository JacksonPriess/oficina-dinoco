package com.dinoco.oficina.autenticacao.infrastructure.gateways;

import com.dinoco.oficina.autenticacao.application.gateways.TokenGateway;
import com.dinoco.oficina.autenticacao.infrastructure.security.TokenService;
import org.springframework.stereotype.Component;

@Component
public class TokenGatewayImpl implements TokenGateway {

    private final TokenService tokenService;

    public TokenGatewayImpl(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public String gerarToken(String username) {
        String token = tokenService.gerarToken(username);
        return token;
    }
}
