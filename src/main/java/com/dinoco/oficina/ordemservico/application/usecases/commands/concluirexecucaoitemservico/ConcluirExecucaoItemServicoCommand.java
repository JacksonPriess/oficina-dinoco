package com.dinoco.oficina.ordemservico.application.usecases.commands.concluirexecucaoitemservico;

import java.time.LocalDateTime;

public record ConcluirExecucaoItemServicoCommand(
        Long osId,
        Long itemId,
        LocalDateTime dataHoraFim
) {}