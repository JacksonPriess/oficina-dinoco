package com.dinoco.oficina.funcionario.infrastructure.gateways;

import com.dinoco.oficina.autenticacao.infrastructure.persistence.UsuarioEntity;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.funcionario.application.gateways.UsuarioSistemaGateway;
import com.dinoco.oficina.funcionario.domain.PerfilUsuario;
import com.dinoco.oficina.funcionario.infrastructure.persistence.UsuarioJpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component

public class UsuarioSistemaGatewayImpl implements UsuarioSistemaGateway {

    private final UsuarioJpaRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public UsuarioSistemaGatewayImpl(UsuarioJpaRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Long criarAcesso(String login, String senhaPura, PerfilUsuario perfil) {
        if (usuarioRepository.existsByLogin(login)) {
            throw new IllegalArgumentException("Este login já está em uso.");
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setLogin(login);
        usuario.setSenha(passwordEncoder.encode(senhaPura));
        usuario.setPrecisaTrocarSenha(true);
        usuario.setPerfil(perfil);

        return usuarioRepository.save(usuario).getId();
    }

    @Override
    public String resetarSenha(Long usuarioId) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        String senhaTemporaria = "Oficina@" + (1000 + SECURE_RANDOM.nextInt(9000));
        usuario.setSenha(passwordEncoder.encode(senhaTemporaria));
        usuario.setPrecisaTrocarSenha(true);

        usuarioRepository.save(usuario);
        return senhaTemporaria;
    }
}
