package com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucaoitemservico;


public record IniciarExecucaoItemServicoCommand(
        Long osId,
        Long itemId
) {}