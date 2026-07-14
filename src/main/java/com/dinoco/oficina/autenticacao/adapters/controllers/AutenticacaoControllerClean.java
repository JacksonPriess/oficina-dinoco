package com.dinoco.oficina.autenticacao.adapters.controllers;

import com.dinoco.oficina.autenticacao.application.usecases.realizarlogin.RealizarLoginCommand;
import com.dinoco.oficina.autenticacao.application.usecases.realizarlogin.RealizarLoginOutput;
import com.dinoco.oficina.autenticacao.application.usecases.realizarlogin.RealizarLoginUseCase;
import com.dinoco.oficina.autenticacao.application.usecases.trocarsenha.TrocarSenhaCommand;
import com.dinoco.oficina.autenticacao.application.usecases.trocarsenha.TrocarSenhaUseCase;

/**
 * Orquestra commands e queries
 */
public class AutenticacaoControllerClean {

    private final RealizarLoginUseCase realizarLoginUseCase;
    private final TrocarSenhaUseCase trocarSenhaUseCase;


    public AutenticacaoControllerClean(RealizarLoginUseCase realizarLoginUseCase,TrocarSenhaUseCase trocarSenhaUseCase) {
        this.realizarLoginUseCase = realizarLoginUseCase;
        this.trocarSenhaUseCase = trocarSenhaUseCase;
    }

    public RealizarLoginOutput realizarLogin(RealizarLoginCommand command) {
        return realizarLoginUseCase.executar(command);
    }

    public void trocarSenha(TrocarSenhaCommand command) {
        trocarSenhaUseCase.executar(command);
    }
}
