package com.dinoco.oficina.util.builders;

import com.dinoco.oficina.autenticacao.infrastructure.persistence.UsuarioEntity;
import com.dinoco.oficina.funcionario.domain.PerfilUsuario;

public class UsuarioBuilder {

    private UsuarioEntity usuario;

    private UsuarioBuilder() {
        this.usuario = new UsuarioEntity();
    }

    public static UsuarioBuilder umUsuario() {
        UsuarioBuilder builder = new UsuarioBuilder();
        builder.usuario.setId(1L);
        builder.usuario.setLogin("funcionario.teste");
        builder.usuario.setSenha("senhaCriptografada123");
        builder.usuario.setAtivo(true);
        builder.usuario.setPrecisaTrocarSenha(false);
        builder.usuario.setPerfil(PerfilUsuario.ADMIN);
        return builder;
    }

    public UsuarioBuilder comId(Long id) {
        this.usuario.setId(id);
        return this;
    }

    public UsuarioEntity build() {
        return this.usuario;
    }
}