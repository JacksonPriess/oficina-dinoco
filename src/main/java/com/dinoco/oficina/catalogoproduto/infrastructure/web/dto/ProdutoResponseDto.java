package com.dinoco.oficina.catalogoproduto.infrastructure.web.dto;

import com.dinoco.oficina.catalogoproduto.domain.TipoProduto;
import java.math.BigDecimal;

public record ProdutoResponseDto(
    Long id,
    Long versao,
    String nome,
    TipoProduto tipo,
    String marca,
    String codigoFabricante,
    String aplicacao,
    BigDecimal quantidadeAtual,
    BigDecimal quantidadeReservada,
    BigDecimal precoVenda,
    BigDecimal precoCusto,
    Boolean ativo
) {}
