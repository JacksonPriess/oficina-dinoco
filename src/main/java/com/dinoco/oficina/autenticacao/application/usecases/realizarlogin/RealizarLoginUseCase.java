package com.dinoco.oficina.autenticacao.application.usecases.realizarlogin;

public interface RealizarLoginUseCase {
    RealizarLoginOutput executar(RealizarLoginCommand input);
}