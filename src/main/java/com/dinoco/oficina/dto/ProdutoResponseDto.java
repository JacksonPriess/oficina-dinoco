package com.dinoco.oficina.dto;

import com.dinoco.oficina.enums.TipoProduto;
import java.math.BigDecimal;

public record ProdutoResponseDto(
    Long id,
    String nome,
    TipoProduto tipo,
    String marca,
    String codigoFabricante,
    String aplicacao,
    BigDecimal quantidadeAtual,
    BigDecimal quantidadeReservada,
    BigDecimal quantidadeDisponivel,
    BigDecimal precoVenda,
    boolean ativo
) {}
