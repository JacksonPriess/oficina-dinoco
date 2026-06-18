package com.dinoco.oficina.funcionario.infrastructure.web.dto;

import com.dinoco.oficina.funcionario.domain.CargoFuncionario;

public record FuncionarioResponseDto(
    Long id,
    String nome,
    String cpf,
    CargoFuncionario cargo,
    boolean ativo
) {}