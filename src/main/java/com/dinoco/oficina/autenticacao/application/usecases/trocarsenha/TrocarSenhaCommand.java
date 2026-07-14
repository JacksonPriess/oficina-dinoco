package com.dinoco.oficina.autenticacao.application.usecases.trocarsenha;

public record TrocarSenhaCommand(Long usuarioId, String novaSenha) {
}