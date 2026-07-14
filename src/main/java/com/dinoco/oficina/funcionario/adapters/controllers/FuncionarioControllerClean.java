package com.dinoco.oficina.funcionario.adapters.controllers;

import com.dinoco.oficina.funcionario.application.usecases.commands.atualizar.AtualizarFuncionarioCommand;
import com.dinoco.oficina.funcionario.application.usecases.commands.atualizar.AtualizarFuncionarioOutput;
import com.dinoco.oficina.funcionario.application.usecases.commands.atualizar.AtualizarFuncionarioUseCase;
import com.dinoco.oficina.funcionario.application.usecases.commands.criar.CriarFuncionarioUseCase;
import com.dinoco.oficina.funcionario.application.usecases.commands.criar.CriarFuncionarioCommand;
import com.dinoco.oficina.funcionario.application.usecases.commands.criar.CriarFuncionarioOutput;
import com.dinoco.oficina.funcionario.application.usecases.commands.desativar.DesativarFuncionarioCommand;
import com.dinoco.oficina.funcionario.application.usecases.commands.desativar.DesativarFuncionarioUseCase;
import com.dinoco.oficina.funcionario.application.usecases.commands.resetarsenha.ResetarSenhaFuncionarioCommand;
import com.dinoco.oficina.funcionario.application.usecases.commands.resetarsenha.ResetarSenhaFuncionarioOutput;
import com.dinoco.oficina.funcionario.application.usecases.commands.resetarsenha.ResetarSenhaFuncionarioUseCase;
import com.dinoco.oficina.funcionario.application.usecases.queries.buscarporid.BuscarFuncionarioPorIdOutput;
import com.dinoco.oficina.funcionario.application.usecases.queries.buscarporid.BuscarFuncionarioPorIdQuery;
import com.dinoco.oficina.funcionario.application.usecases.queries.buscarporid.BuscarFuncionarioPorIdUseCase;

/**
 * Orquestra commands e queries
 */
public class FuncionarioControllerClean {

    private final CriarFuncionarioUseCase criarFuncionarioUseCase;
    private final AtualizarFuncionarioUseCase atualizarFuncionarioUseCase;
    private final DesativarFuncionarioUseCase desativarFuncionarioUseCase;
    private final BuscarFuncionarioPorIdUseCase buscarFuncionarioPorIdUseCase;

    private final ResetarSenhaFuncionarioUseCase resetarSenhaUseCase;


    public FuncionarioControllerClean(CriarFuncionarioUseCase criarFuncionarioUseCase, AtualizarFuncionarioUseCase atualizarFuncionarioUseCase, DesativarFuncionarioUseCase desativarFuncionarioUseCase, BuscarFuncionarioPorIdUseCase buscarFuncionarioPorIdUseCase, ResetarSenhaFuncionarioUseCase resetarSenhaUseCase) {
        this.criarFuncionarioUseCase = criarFuncionarioUseCase;
        this.atualizarFuncionarioUseCase = atualizarFuncionarioUseCase;
        this.desativarFuncionarioUseCase = desativarFuncionarioUseCase;
        this.buscarFuncionarioPorIdUseCase = buscarFuncionarioPorIdUseCase;
        this.resetarSenhaUseCase = resetarSenhaUseCase;

    }

    public CriarFuncionarioOutput criarFuncionario(CriarFuncionarioCommand command) {
        return criarFuncionarioUseCase.executar(command);
    }

    public AtualizarFuncionarioOutput atualizarFuncionario(AtualizarFuncionarioCommand command) {
        return atualizarFuncionarioUseCase.executar(command);
    }

    public void desativarFuncionario(DesativarFuncionarioCommand command) {
        desativarFuncionarioUseCase.executar(command);
    }

    public BuscarFuncionarioPorIdOutput buscarPorId(BuscarFuncionarioPorIdQuery query) {
        return buscarFuncionarioPorIdUseCase.executar(query);
    }

    public ResetarSenhaFuncionarioOutput resetarSenha(ResetarSenhaFuncionarioCommand command) {
        return resetarSenhaUseCase.executar(command);
    }

}