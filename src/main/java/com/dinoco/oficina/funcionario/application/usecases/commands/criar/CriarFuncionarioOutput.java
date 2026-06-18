package com.dinoco.oficina.funcionario.application.usecases.commands.criar;

import com.dinoco.oficina.funcionario.domain.CargoFuncionario;

public record CriarFuncionarioOutput(
        Long id,
        String nome,
        String cpf,
        CargoFuncionario cargo,
        Boolean ativo
) {}

