package com.dinoco.oficina.ordemservico.application.usecases.commands.atualizarstatus;

public record AtualizarStatusCommand(
        String codigoRastreio,
        String acao,
        String laudo
) {}