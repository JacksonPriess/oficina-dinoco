package com.dinoco.oficina.autenticacao.application.usecases.trocarsenha;

import com.dinoco.oficina.autenticacao.application.gateways.PasswordEncoderGateway;
import com.dinoco.oficina.autenticacao.application.gateways.UsuarioAutenticacaoGateway;

public class TrocarSenhaHandler implements TrocarSenhaUseCase {

    private final UsuarioAutenticacaoGateway usuarioGateway;
    private final PasswordEncoderGateway passwordEncoderGateway;

    public TrocarSenhaHandler(UsuarioAutenticacaoGateway usuarioGateway, PasswordEncoderGateway passwordEncoderGateway) {
        this.usuarioGateway = usuarioGateway;
        this.passwordEncoderGateway = passwordEncoderGateway;
    }

    @Override
    public void executar(TrocarSenhaCommand command) {
        // 1. O Use Case orquestra a criptografia via contrato (sem conhecer o BCrypt)
        String senhaCriptografada = passwordEncoderGateway.criptografar(command.novaSenha());

        // 2. Manda atualizar no banco de dados e tira a obrigatoriedade de troca (Regra de Negócio)
        usuarioGateway.atualizarCredenciais(command.usuarioId(), senhaCriptografada, false);
    }
}
