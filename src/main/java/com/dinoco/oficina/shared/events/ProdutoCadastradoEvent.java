package com.dinoco.oficina.shared.events;

import java.math.BigDecimal;

public record ProdutoCadastradoEvent(
        Long produtoId,
        BigDecimal quantidade
) {}