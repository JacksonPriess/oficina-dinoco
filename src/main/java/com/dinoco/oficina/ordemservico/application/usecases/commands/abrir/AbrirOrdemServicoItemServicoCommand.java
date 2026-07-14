package com.dinoco.oficina.ordemservico.application.usecases.commands.abrir;

public record AbrirOrdemServicoItemServicoCommand(
        Long servicoId,
        Long mecanicoId
) {}