package com.dinoco.oficina.ordemservico.application.usecases.commands.abrir;

import com.dinoco.oficina.ordemservico.domain.enums.StatusOS;

public record AbrirOrdemServicoOutput(
        Long osId,
        String codigoRastreio,
        Long clienteId,
        Long veiculoId,
        StatusOS status,
        String reclamacaoCliente,
        Integer quilometragemEntrada
) {}


