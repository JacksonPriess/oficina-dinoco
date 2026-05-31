package com.dinoco.oficina.catalogoproduto.application.usecases.queries.buscarporid;

import com.dinoco.oficina.catalogoproduto.domain.TipoProduto;
import java.math.BigDecimal;

public record BuscarProdutoPorIdOutput(
        Long id,
        Long versao,
        String nome,
        TipoProduto tipo,
        String marca,
        String codigoFabricante,
        String aplicacao,
        BigDecimal quantidadeAtual,
        BigDecimal quantidadeReservada,
        BigDecimal precoCusto,
        BigDecimal precoVenda,
        Boolean ativo
) {}
