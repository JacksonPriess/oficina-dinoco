package com.dinoco.oficina.dto;

import com.dinoco.oficina.enums.TipoProduto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record ProdutoUpdateRequestDto(

        @NotBlank(message = "O nome do produto é obrigatório.")
        String nome,

        @NotNull(message = "O tipo do produto é obrigatório.")
        TipoProduto tipo,

        String marca,
        String codigoFabricante,
        String aplicacao,

        @PositiveOrZero
        BigDecimal quantidadeAtual,

        @PositiveOrZero
        BigDecimal precoCusto,

        @PositiveOrZero
        BigDecimal precoVenda
) {}