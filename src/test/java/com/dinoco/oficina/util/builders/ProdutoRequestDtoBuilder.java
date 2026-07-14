package com.dinoco.oficina.util.builders;

import com.dinoco.oficina.catalogoproduto.domain.TipoProduto;
import com.dinoco.oficina.catalogoproduto.infrastructure.web.dto.ProdutoRequestDto;

import java.math.BigDecimal;

public class ProdutoRequestDtoBuilder {

    private ProdutoRequestDtoBuilder() {}

    public static ProdutoRequestDto criarSemEstoqueInicial() {
        return new ProdutoRequestDto(
                "Pastilha de Freio",
                TipoProduto.PECA,
                "Cobreq",
                "C999",
                "Freio dianteiro universal",
                BigDecimal.ZERO, // Sem estoque inicial
                new BigDecimal("50.00"),
                new BigDecimal("120.00")
        );
    }

    public static ProdutoRequestDto criarComEstoqueInicial(BigDecimal quantidade) {
        return new ProdutoRequestDto(
                "Óleo de Motor 5W40",
                TipoProduto.INSUMO,
                "Castrol",
                "O5W40",
                "Motor",
                quantidade, // Estoque inicial
                new BigDecimal("30.00"),
                new BigDecimal("60.00")
        );
    }
}