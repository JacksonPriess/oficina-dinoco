package com.dinoco.oficina.ordemservico.application.usecases.commands.decisaoclienteautenticado;

public record DecisaoClienteAutenticadoCommand(
        String codigoRastreio,
        Long clienteId,
        String statusDecisao,
        String observacao
) {}