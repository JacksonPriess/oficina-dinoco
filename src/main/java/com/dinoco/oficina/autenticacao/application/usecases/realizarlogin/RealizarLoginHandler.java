package com.dinoco.oficina.autenticacao.application.usecases.realizarlogin;

import com.dinoco.oficina.autenticacao.application.gateways.AutenticacaoCommandGateway;
import com.dinoco.oficina.autenticacao.application.gateways.TokenGateway;

public class RealizarLoginHandler implements RealizarLoginUseCase {

    private final AutenticacaoCommandGateway autenticacaoCommandGateway;
    private final TokenGateway tokenGateway;

    public RealizarLoginHandler(AutenticacaoCommandGateway autenticacaoCommandGateway, TokenGateway tokenGateway) {
        this.autenticacaoCommandGateway = autenticacaoCommandGateway;
        this.tokenGateway = tokenGateway;
    }

    @Override
    public RealizarLoginOutput executar(RealizarLoginCommand input) {
        boolean autenticado = autenticacaoCommandGateway.autenticar(input.username(), input.password());
        if (!autenticado) {
            throw new RuntimeException("Credenciais inválidas");
        }
        String token = tokenGateway.gerarToken(input.username());
        return new RealizarLoginOutput(token);
    }

}
