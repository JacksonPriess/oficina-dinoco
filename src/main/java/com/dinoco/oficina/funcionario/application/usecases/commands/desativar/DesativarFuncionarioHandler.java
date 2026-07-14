package com.dinoco.oficina.funcionario.application.usecases.commands.desativar;

import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.funcionario.application.gateways.FuncionarioCommandGateway;
import com.dinoco.oficina.funcionario.domain.Funcionario;

public class DesativarFuncionarioHandler implements DesativarFuncionarioUseCase {

    private final FuncionarioCommandGateway funcionarioCommandGateway;

    public DesativarFuncionarioHandler(FuncionarioCommandGateway funcionarioCommandGateway) {
        this.funcionarioCommandGateway = funcionarioCommandGateway;
    }

    @Override
    public void executar(DesativarFuncionarioCommand command) {

        Funcionario funcionario = funcionarioCommandGateway.buscarParaAlteracao(command.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário não encontrado com ID: " + command.id()));

        funcionario.desativar();

        funcionarioCommandGateway.salvar(funcionario);
    }
}

