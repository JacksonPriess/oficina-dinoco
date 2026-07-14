package com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemservico;

import java.math.BigDecimal;

public record AlterarItemServicoCommand(
        Long osId,
        Long itemId,
        BigDecimal valorCobrado,
        Long mecanicoId
) {}