package com.dinoco.oficina.dto;

import jakarta.validation.constraints.NotNull;

public record OrdemServicoRequestDto(
        @NotNull
        Long clienteId,

        @NotNull
        Long veiculoId,

        @NotNull
        Integer quilometragemEntrada,

        @NotNull
        String reclamacaoCliente
) {}
