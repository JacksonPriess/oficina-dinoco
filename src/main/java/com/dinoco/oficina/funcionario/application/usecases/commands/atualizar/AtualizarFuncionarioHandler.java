package com.dinoco.oficina.funcionario.application.usecases.commands.atualizar;

import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.funcionario.application.gateways.FuncionarioCommandGateway;
import com.dinoco.oficina.funcionario.domain.Funcionario;

public class AtualizarFuncionarioHandler implements AtualizarFuncionarioUseCase {

    private final FuncionarioCommandGateway funcionarioCommandGateway;

    public AtualizarFuncionarioHandler(FuncionarioCommandGateway funcionarioCommandGateway) {
        this.funcionarioCommandGateway = funcionarioCommandGateway;
    }

    @Override
    public AtualizarFuncionarioOutput executar(AtualizarFuncionarioCommand command) {

        Funcionario funcionario = funcionarioCommandGateway.buscarParaAlteracao(command.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário não encontrado com ID: " + command.id()));

        if (!funcionario.getCpf().equals(command.cpf())) {
            throw new IllegalArgumentException("Não é permitido alterar o documento (CPF) de um funcionário já cadastrado.");
        }

        funcionario.atualizarDados(
                command.nome(),
                command.cargo());

        Funcionario funcionarioSalvo = funcionarioCommandGateway.salvar(funcionario);

        return mapearParaOutput(funcionarioSalvo);
    }

    private AtualizarFuncionarioOutput mapearParaOutput(Funcionario funcionario) {
        return new AtualizarFuncionarioOutput(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getCpf(),
                funcionario.getCargo(),
                funcionario.isAtivo()
        );
    }
}
