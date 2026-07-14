package com.dinoco.oficina.autenticacao.infrastructure.gateways;

import com.dinoco.oficina.autenticacao.application.gateways.AutenticacaoCommandGateway;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class AutenticacaoCommandGatewayImpl implements AutenticacaoCommandGateway {

    private final AuthenticationManager authenticationManager;

    public AutenticacaoCommandGatewayImpl(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public boolean autenticar(String username, String password) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(username, password);
        var auth = this.authenticationManager.authenticate(usernamePassword);
        return auth.isAuthenticated();
    }
}
