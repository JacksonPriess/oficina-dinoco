package com.dinoco.oficina.funcionario.application.usecases.commands.criar;

import com.dinoco.oficina.funcionario.application.gateways.FuncionarioCommandGateway;
import com.dinoco.oficina.funcionario.application.gateways.FuncionarioQueryGateway;
import com.dinoco.oficina.funcionario.application.gateways.UsuarioSistemaGateway;
import com.dinoco.oficina.funcionario.domain.Funcionario;

public class CriarFuncionarioHandler implements CriarFuncionarioUseCase {

    private final FuncionarioCommandGateway funcionarioCommandGateway;
    private final FuncionarioQueryGateway funcionarioQueryGateway;
    private final UsuarioSistemaGateway usuarioGateway;

    public CriarFuncionarioHandler(FuncionarioCommandGateway funcionarioCommandGateway, FuncionarioQueryGateway funcionarioQueryGateway, UsuarioSistemaGateway usuarioGateway) {
        this.funcionarioCommandGateway = funcionarioCommandGateway;
        this.funcionarioQueryGateway = funcionarioQueryGateway;
        this.usuarioGateway = usuarioGateway;
    }

    @Override
    public CriarFuncionarioOutput executar(CriarFuncionarioCommand command) {

        if (funcionarioQueryGateway.existePorCpf(command.cpf())) {
            throw new IllegalArgumentException("Funcionário já cadastrado com este CPF.");
        }

        Funcionario novoFuncionario = new Funcionario(
                command.nome(),
                command.cpf(),
                command.cargo()
        );

        if (command.criarAcesso()) {
            Long usuarioId = usuarioGateway.criarAcesso(
                    command.login(),
                    command.senha(),
                    novoFuncionario.definirPerfilAcesso()
            );
            novoFuncionario.vincularUsuario(usuarioId);
        }

        Funcionario funcionarioSalvo = funcionarioCommandGateway.salvar(novoFuncionario);

        return mapearParaOutput(funcionarioSalvo);
    }

    private CriarFuncionarioOutput mapearParaOutput(Funcionario funcionario) {
        return new CriarFuncionarioOutput(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getCpf(),
                funcionario.getCargo(),
                funcionario.isAtivo()
        );
    }
}

