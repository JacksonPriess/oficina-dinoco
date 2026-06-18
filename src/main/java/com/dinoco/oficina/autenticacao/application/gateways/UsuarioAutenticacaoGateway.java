package com.dinoco.oficina.autenticacao.application.gateways;

public interface UsuarioAutenticacaoGateway {
    void atualizarCredenciais(Long usuarioId, String senhaCriptografada, boolean precisaTrocarSenha);
}
