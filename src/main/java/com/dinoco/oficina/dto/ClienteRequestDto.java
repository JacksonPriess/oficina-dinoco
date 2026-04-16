package com.dinoco.oficina.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record ClienteRequestDto(
        @NotBlank(message = "O tipo de pessoa é obrigatório.")
        @Pattern(regexp = "^[FJ]$", message = "O tipo de pessoa deve ser 'F' para Física ou 'J' para Jurídica.")
        String tipoPessoa,
        @NotBlank(message = "O documento é obrigatório.")
        @Pattern(regexp = "^[A-Za-z0-9]{11,14}$", message = "O documento deve conter apenas números e letras (sem pontuação).")
        String documento,
        String inscricaoEstadual,
        @NotBlank(message = "O nome é obrigatório.")
        String nome,
        String nomeFantasia,
        @NotBlank(message = "O email é obrigatório.")
        String email,
        @NotBlank(message = "O telefone é obrigatório.")
        String telefone,
        List<EnderecoDto> enderecos
) {}
