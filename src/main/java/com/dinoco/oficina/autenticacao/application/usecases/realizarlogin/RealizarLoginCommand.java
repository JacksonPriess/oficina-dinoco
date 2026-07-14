package com.dinoco.oficina.autenticacao.application.usecases.realizarlogin;

public record RealizarLoginCommand(
        String username,
        String password)
{}