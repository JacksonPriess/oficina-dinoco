package com.dinoco.oficina.ordemservico.application.usecases.commands.abrir;

import java.math.BigDecimal;

public record AbrirOrdemServicoItemProdutoCommand(
        Long produtoId,
        BigDecimal quantidade
) {}