package com.dinoco.oficina.funcionario.application.usecases.queries.buscarporid;


import com.dinoco.oficina.funcionario.domain.CargoFuncionario;

public record BuscarFuncionarioPorIdOutput(
        Long id,
        String nome,
        String cpf,
        CargoFuncionario cargo,
        Boolean ativo,
        Long usuarioId
) {}

