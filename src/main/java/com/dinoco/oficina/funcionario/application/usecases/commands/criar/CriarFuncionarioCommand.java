package com.dinoco.oficina.funcionario.application.usecases.commands.criar;


import com.dinoco.oficina.funcionario.domain.CargoFuncionario;

public record CriarFuncionarioCommand(
        String nome,
        String cpf,
        CargoFuncionario cargo,
        Boolean criarAcesso,
        String login,
        String senha
) {}

