package com.dinoco.oficina.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record FuncionarioRequestDto(
        @NotBlank(message = "O nome é obrigatório.")
        String nome,

        @Pattern(regexp = "^[0-9]{11}$", message = "O cpf deve conter apenas números (sem pontuação).")
        String cpf,

        @NotBlank(message = "O cargo é obrigatório.")
        String cargo,

        boolean criarAcesso,
        String login,
        String senha
) {}