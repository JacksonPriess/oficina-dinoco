package com.dinoco.oficina.autenticacao.infrastructure.config;

import com.dinoco.oficina.autenticacao.adapters.controllers.AutenticacaoControllerClean;
import com.dinoco.oficina.autenticacao.application.gateways.AutenticacaoCommandGateway;
import com.dinoco.oficina.autenticacao.application.gateways.PasswordEncoderGateway;
import com.dinoco.oficina.autenticacao.application.gateways.TokenGateway;
import com.dinoco.oficina.autenticacao.application.gateways.UsuarioAutenticacaoGateway;
import com.dinoco.oficina.autenticacao.application.usecases.realizarlogin.RealizarLoginHandler;
import com.dinoco.oficina.autenticacao.application.usecases.realizarlogin.RealizarLoginUseCase;
import com.dinoco.oficina.autenticacao.application.usecases.trocarsenha.TrocarSenhaHandler;
import com.dinoco.oficina.autenticacao.application.usecases.trocarsenha.TrocarSenhaUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutenticacaoConfig {

    @Bean
    public RealizarLoginUseCase realizarLoginUseCase(AutenticacaoCommandGateway autenticacaoCommandGateway, TokenGateway tokenGateway) {
        return new RealizarLoginHandler(autenticacaoCommandGateway, tokenGateway);
    }

    @Bean
    public TrocarSenhaUseCase trocarSenhaUseCase(UsuarioAutenticacaoGateway usuarioGateway, PasswordEncoderGateway passwordEncoderGateway) {
        return new TrocarSenhaHandler(usuarioGateway, passwordEncoderGateway);
    }

    @Bean
    public AutenticacaoControllerClean autenticacaoControllerClean(RealizarLoginUseCase realizarLoginUseCase, TrocarSenhaUseCase trocarSenhaUseCase) {
        return new AutenticacaoControllerClean(realizarLoginUseCase, trocarSenhaUseCase);
    }

}
