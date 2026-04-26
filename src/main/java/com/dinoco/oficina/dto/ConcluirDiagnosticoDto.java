package com.dinoco.oficina.dto;

import jakarta.validation.constraints.NotBlank;

public record ConcluirDiagnosticoDto(
        @NotBlank(message = "O laudo técnico é obrigatório e não pode estar em branco.")
        String laudo
) {}
