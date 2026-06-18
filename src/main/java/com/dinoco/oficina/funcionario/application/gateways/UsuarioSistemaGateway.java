package com.dinoco.oficina.funcionario.application.gateways;

import com.dinoco.oficina.funcionario.domain.PerfilUsuario;

public interface UsuarioSistemaGateway {
    Long criarAcesso(String login, String senhaPura, PerfilUsuario perfil);
    String resetarSenha(Long usuarioId);
}
