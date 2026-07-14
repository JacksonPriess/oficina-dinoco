package com.dinoco.oficina.estoque.infrastructure.web.dto;

import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record EntradaSaldoEstoqueRequestDto(
    @PositiveOrZero
    BigDecimal quantidadeEntrada,
    String observacao
) {}
