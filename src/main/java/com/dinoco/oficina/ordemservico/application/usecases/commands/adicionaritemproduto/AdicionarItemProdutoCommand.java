package com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemproduto;

import java.math.BigDecimal;

public record AdicionarItemProdutoCommand(
        Long osId,
        Long produtoId,
        BigDecimal quantidade
) {}
