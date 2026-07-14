package com.dinoco.oficina.autenticacao.infrastructure.gateways;

import com.dinoco.oficina.autenticacao.application.gateways.UsuarioAutenticacaoGateway;
import com.dinoco.oficina.autenticacao.infrastructure.persistence.UsuarioEntity;
import com.dinoco.oficina.autenticacao.infrastructure.persistence.UsuarioRepository;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class UsuarioAutenticacaoGatewayImpl implements UsuarioAutenticacaoGateway {

    private final UsuarioRepository usuarioRepository;

    public UsuarioAutenticacaoGatewayImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public void atualizarCredenciais(Long usuarioId, String senhaCriptografada, boolean precisaTrocarSenha) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        usuario.setSenha(senhaCriptografada);
        usuario.setPrecisaTrocarSenha(precisaTrocarSenha);
        usuarioRepository.save(usuario);
    }
}
