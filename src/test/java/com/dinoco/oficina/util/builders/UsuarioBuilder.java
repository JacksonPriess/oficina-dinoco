package com.dinoco.oficina.util.builders;

import com.dinoco.oficina.entity.Usuario;

public class UsuarioBuilder {

    private Usuario usuario;

    private UsuarioBuilder() {
        this.usuario = new Usuario();
    }

    public static UsuarioBuilder umUsuario() {
        UsuarioBuilder builder = new UsuarioBuilder();
        builder.usuario.setId(1L);
        builder.usuario.setLogin("funcionario.teste");
        builder.usuario.setSenha("senhaCriptografada123");
        builder.usuario.setAtivo(true);
        return builder;
    }

    public UsuarioBuilder comId(Long id) {
        this.usuario.setId(id);
        return this;
    }

    public Usuario build() {
        return this.usuario;
    }
}