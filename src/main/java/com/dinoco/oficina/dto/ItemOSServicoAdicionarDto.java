package com.dinoco.oficina.dto;

import jakarta.validation.constraints.NotNull;

public record ItemOSServicoAdicionarDto(
        @NotNull
        Long servicoId,

        Long mecanicoId
) {}