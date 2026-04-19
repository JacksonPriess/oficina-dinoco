package com.dinoco.oficina.dto;

public record FuncionarioResponseDto(
    Long id,
    String nome,
    String cpf,
    String cargo,
    boolean ativo
) {}