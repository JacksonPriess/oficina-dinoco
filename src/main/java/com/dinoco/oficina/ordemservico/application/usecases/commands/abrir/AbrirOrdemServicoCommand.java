package com.dinoco.oficina.ordemservico.application.usecases.commands.abrir;

public record AbrirOrdemServicoCommand(
        Long clienteId,
        Long veiculoId,
        Integer quilometragemEntrada,
        String reclamacaoCliente
) {}
