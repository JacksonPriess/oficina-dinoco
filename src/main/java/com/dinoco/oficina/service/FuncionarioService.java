package com.dinoco.oficina.service;

import com.dinoco.oficina.dto.FuncionarioRequestDto;
import com.dinoco.oficina.dto.FuncionarioResponseDto;
import com.dinoco.oficina.entity.Funcionario;
import com.dinoco.oficina.entity.Usuario;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.repository.FuncionarioRepository;
import com.dinoco.oficina.util.DocumentoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FuncionarioService {

    private final FuncionarioRepository repository;
    private final UsuarioService usuarioService;

    @Transactional
    public FuncionarioResponseDto criar(FuncionarioRequestDto dto) {

        if (repository.existsByCpf(dto.cpf())) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }

        if (!DocumentoUtil.isCpfValido(dto.cpf())) {
            throw new IllegalArgumentException("CPF inválido.");
        }

        Funcionario funcionario = new Funcionario();
        funcionario.setNome(dto.nome());
        funcionario.setCpf(dto.cpf());
        funcionario.setCargo(dto.cargo());

        if (dto.criarAcesso()) {
            Usuario usuarioSalvo = usuarioService.criarUsuarioSistema(dto.login(), dto.senha());
            funcionario.setUsuarioId(usuarioSalvo.getId());
        }
        Funcionario funcionarioSalvo = repository.save(funcionario);
        return mapearParaResponse(funcionarioSalvo);
    }

    public FuncionarioResponseDto buscarPorId(Long id) {
        Funcionario funcionario = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário não encontrado."));
        return mapearParaResponse(funcionario);
    }

    public Funcionario buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário não encontrado com ID: " + id));
    }

    @Transactional
    public FuncionarioResponseDto atualizar(Long id, FuncionarioRequestDto dto) {
        Funcionario funcionario = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário não encontrado."));

        if (!funcionario.getCpf().equals(dto.cpf())) {
            throw new IllegalArgumentException("Não é permitido alterar o cpf de um funcionário já cadastrado.");
        }

        funcionario.setNome(dto.nome());
        funcionario.setCpf(dto.cpf());
        funcionario.setCargo(dto.cargo());

        Funcionario atualizado = repository.save(funcionario);
        return mapearParaResponse(atualizado);
    }

    @Transactional
    public void desativar(Long id) {
        Funcionario funcionario = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário não encontrado."));
        funcionario.setAtivo(false);
        repository.save(funcionario);
    }

    @Transactional
    public String resetarSenhaFuncionario(Long funcionarioId) {
        Funcionario funcionario = repository.findById(funcionarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário não encontrado"));

        if (funcionario.getUsuarioId() == null) {
            throw new IllegalArgumentException("Este funcionário não possui acesso ao sistema.");
        }

        return usuarioService.resetarSenhaGerandoTemporaria(funcionario.getUsuarioId());
    }

    private FuncionarioResponseDto mapearParaResponse(Funcionario funcionario) {
        return new FuncionarioResponseDto(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getCpf(),
                funcionario.getCargo(),
                funcionario.isAtivo()
        );
    }


}
