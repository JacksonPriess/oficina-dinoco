package com.dinoco.oficina.service;

import com.dinoco.oficina.entity.Usuario;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Transactional
    public Usuario criarUsuarioSistema(String login, String senhaPura) {

        if (usuarioRepository.existsByLogin(login)) {
            throw new IllegalArgumentException("Este login já está em uso.");
        }

        Usuario usuario = new Usuario();
        usuario.setLogin(login);
        usuario.setSenha(passwordEncoder.encode(senhaPura));

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public String resetarSenhaGerandoTemporaria(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        String senhaTemporaria = "Oficina@" + (1000 + SECURE_RANDOM.nextInt(9000));

        usuario.setSenha(passwordEncoder.encode(senhaTemporaria));
        usuarioRepository.save(usuario);

        return senhaTemporaria;
    }
}