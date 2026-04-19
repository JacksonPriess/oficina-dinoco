package com.dinoco.oficina.dto;

import com.dinoco.oficina.enums.TipoProduto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProdutoRequestDto(

    @NotBlank(message = "O nome do produto é obrigatório.")
    String nome,

    @NotNull
    TipoProduto tipo,

    String marca,
    String codigoFabricante,
    String aplicacao,

    @NotNull
    @PositiveOrZero
    BigDecimal precoCusto,

    @NotNull
    @PositiveOrZero
    BigDecimal precoVenda,

    @NotNull
    @PositiveOrZero
    BigDecimal quantidadeInicial
) {}
