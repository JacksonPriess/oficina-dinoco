package com.dinoco.oficina.ordemservico.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record DecisaoClienteRequestDto(
        @NotBlank(message = "O status da decisão é obrigatório ( APROVADO, RECUSADO)")
        String statusDecisao,

        String observacao
) {}