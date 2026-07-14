package com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemservico;

public record AdicionarItemServicoCommand(
        Long osId,
        Long servicoId,
        Long mecanicoId
) {}