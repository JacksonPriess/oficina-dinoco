package com.dinoco.oficina.funcionario.application.usecases.commands.resetarsenha;

import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.funcionario.application.gateways.FuncionarioCommandGateway;
import com.dinoco.oficina.funcionario.application.gateways.UsuarioSistemaGateway;
import com.dinoco.oficina.funcionario.domain.Funcionario;

public class ResetarSenhaFuncionarioHandler implements ResetarSenhaFuncionarioUseCase {

    private final FuncionarioCommandGateway funcionarioCommandGateway;
    private final UsuarioSistemaGateway usuarioSistemaGateway;

    public ResetarSenhaFuncionarioHandler(FuncionarioCommandGateway funcionarioCommandGateway,UsuarioSistemaGateway usuarioSistemaGateway) {
        this.funcionarioCommandGateway = funcionarioCommandGateway;
        this.usuarioSistemaGateway = usuarioSistemaGateway;
    }

    @Override
    public ResetarSenhaFuncionarioOutput executar(ResetarSenhaFuncionarioCommand command) {

        Funcionario funcionario = funcionarioCommandGateway.buscarParaAlteracao(command.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário não encontrado com ID: " + command.id()));

        if (funcionario.getUsuarioId() == null) {
            throw new IllegalArgumentException("Este funcionário não possui acesso ao sistema.");
        }

        String senhaNova = usuarioSistemaGateway.resetarSenha(funcionario.getUsuarioId());

        return new ResetarSenhaFuncionarioOutput(senhaNova);
    }
}

