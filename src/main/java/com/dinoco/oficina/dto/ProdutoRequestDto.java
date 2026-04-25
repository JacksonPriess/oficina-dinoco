package com.dinoco.oficina.dto;

import com.dinoco.oficina.enums.TipoProduto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record ProdutoRequestDto(

    @NotBlank(message = "O nome do produto é obrigatório.")
    String nome,

    @NotNull(message = "O tipo do produto é obrigatório PECA/INSUMO.")
    TipoProduto tipo,

    String marca,
    String codigoFabricante,
    String aplicacao,

    @PositiveOrZero
    BigDecimal quantidadeAtual,

    @PositiveOrZero
    BigDecimal quantidadeReservada,

    @PositiveOrZero
    BigDecimal precoCusto,

    @PositiveOrZero
    BigDecimal precoVenda
) {
    public ProdutoRequestDto {
        quantidadeAtual = quantidadeAtual != null ? quantidadeAtual : BigDecimal.ZERO;
        quantidadeReservada = quantidadeReservada != null ? quantidadeReservada : BigDecimal.ZERO;
        precoCusto = precoCusto != null ? precoCusto : BigDecimal.ZERO;
        precoVenda = precoVenda != null ? precoVenda : BigDecimal.ZERO;
    }
}
