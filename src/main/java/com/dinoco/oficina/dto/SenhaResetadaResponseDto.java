package com.dinoco.oficina.dto;

// Usado pelo gerente para ver a nova senha gerada
public record SenhaResetadaResponseDto(
        String novaSenhaTemporaria
) {}
