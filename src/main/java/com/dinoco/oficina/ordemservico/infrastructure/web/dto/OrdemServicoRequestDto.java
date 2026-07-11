package com.dinoco.oficina.ordemservico.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrdemServicoRequestDto(
        @NotNull
        Long clienteId,

        @NotNull
        Long veiculoId,

        @NotNull
        Integer quilometragemEntrada,

        @NotNull
        String reclamacaoCliente,

        List<ItemOSProdutoAdicionarDto> produtos,

        List<ItemOSServicoAdicionarDto> servicos
) {}