package com.dinoco.oficina.estoque.application.usecases.commands.registrarentrada;

import java.math.BigDecimal;

public record RegistrarEntradaCommand(
        Long produtoId,
        BigDecimal quantidade,
        String observacao
) {}