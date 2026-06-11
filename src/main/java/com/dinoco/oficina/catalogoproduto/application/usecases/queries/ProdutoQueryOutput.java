package com.dinoco.oficina.catalogoproduto.application.usecases.queries;

import java.math.BigDecimal;

public record ProdutoQueryOutput(
        Long id,
        Long versao,
        String nome,
        String tipo,
        String marca,
        String codigoFabricante,
        String aplicacao,
        BigDecimal precoCusto,
        BigDecimal precoVenda,
        Boolean ativo,
        BigDecimal quantidadeReal,
        BigDecimal quantidadeReservada
) {}