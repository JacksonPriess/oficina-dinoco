package com.dinoco.oficina.ordemservico.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;

public record OrdemServicoAbrirRequestDto(
        @NotNull
        Long clienteId,

        @NotNull
        Long veiculoId,

        @NotNull
        Integer quilometragemEntrada,

        @NotNull
        String reclamacaoCliente
) {}
