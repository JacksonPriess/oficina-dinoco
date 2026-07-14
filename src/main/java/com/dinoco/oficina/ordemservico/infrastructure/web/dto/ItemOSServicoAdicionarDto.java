package com.dinoco.oficina.ordemservico.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;

public record ItemOSServicoAdicionarDto(
        @NotNull(message = "O ID do serviço é obrigatório")
        Long servicoId,

        Long mecanicoId
) {}