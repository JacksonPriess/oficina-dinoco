package com.dinoco.oficina.ordemservico.application.usecases.commands.decisaoclienteorcamento;

public record DecisaoClienteCommand(
        String codigoRastreio,
        String statusDecisao,
        String observacao
) {}