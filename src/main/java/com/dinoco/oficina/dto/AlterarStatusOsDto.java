package com.dinoco.oficina.dto;

import com.dinoco.oficina.enums.StatusOS;
import jakarta.validation.constraints.NotNull;

// DTO para Mudar o Status
public record AlterarStatusOsDto(
        @NotNull
        StatusOS novoStatus
) {}
