package com.dinoco.oficina.estoque.application.usecases.commands.ajustarinventario;

import java.math.BigDecimal;

public record AjustarInventarioCommand(
        Long produtoId,
        BigDecimal diferenca,
        String observacao
) {}