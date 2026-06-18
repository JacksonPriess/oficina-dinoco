package com.dinoco.oficina.service;

/*
import com.dinoco.oficina.dto.FuncionarioRequestDto;
import com.dinoco.oficina.dto.FuncionarioResponseDto;
import com.dinoco.oficina.entity.Funcionario;
import com.dinoco.oficina.entity.Usuario;
import com.dinoco.oficina.enums.CargoFuncionario;
import com.dinoco.oficina.enums.PerfilUsuario;
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
    private static final String MSG_FUNCIONARIO_NAO_ENCONTRADO = "Funcionário não encontrado.";


    private PerfilUsuario definirPerfilPorCargo(CargoFuncionario cargo) {
        return switch (cargo) {
            case MECANICO -> PerfilUsuario.MECANICO;
            case ATENDENTE -> PerfilUsuario.ATENDENTE;
        };
    }

    public FuncionarioResponseDto buscarPorId(Long id) {
        Funcionario funcionario = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(MSG_FUNCIONARIO_NAO_ENCONTRADO));
        return mapearParaResponse(funcionario);
    }

    public Funcionario buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário não encontrado com ID: " + id));
    }



    @Transactional
    public void desativar(Long id) {
        Funcionario funcionario = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(MSG_FUNCIONARIO_NAO_ENCONTRADO));
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


 */