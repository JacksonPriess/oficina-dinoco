package com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemproduto;

import java.math.BigDecimal;

public record AlterarItemProdutoCommand(
        Long osId,
        Long itemId,
        BigDecimal precoVenda,
        BigDecimal quantidade
) {}

