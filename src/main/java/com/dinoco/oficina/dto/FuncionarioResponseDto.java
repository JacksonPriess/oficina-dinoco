package com.dinoco.oficina.dto;

import com.dinoco.oficina.enums.CargoFuncionario;

public record FuncionarioResponseDto(
    Long id,
    String nome,
    String cpf,
    CargoFuncionario cargo,
    boolean ativo
) {}