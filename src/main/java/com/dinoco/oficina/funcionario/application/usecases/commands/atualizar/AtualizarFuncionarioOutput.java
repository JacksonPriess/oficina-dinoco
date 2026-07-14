package com.dinoco.oficina.funcionario.application.usecases.commands.atualizar;

import com.dinoco.oficina.funcionario.domain.CargoFuncionario;

public record AtualizarFuncionarioOutput(
        Long id,
        String nome,
        String cpf,
        CargoFuncionario cargo,
        Boolean ativo
) {}
