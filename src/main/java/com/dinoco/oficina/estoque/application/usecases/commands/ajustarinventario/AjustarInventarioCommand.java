package com.dinoco.oficina.estoque.application.usecases.commands.ajustarinventario;

import java.math.BigDecimal;

public record AjustarInventarioCommand(
        Long produtoId,
        Long versao,
        BigDecimal quantidadeContadaNaPrateleira,
        String observacao
) {}