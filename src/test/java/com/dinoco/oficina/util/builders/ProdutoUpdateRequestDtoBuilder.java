package com.dinoco.oficina.util.builders;

import com.dinoco.oficina.dto.ProdutoUpdateRequestDto;
import com.dinoco.oficina.enums.TipoProduto;
import java.math.BigDecimal;

public class ProdutoUpdateRequestDtoBuilder {

    private ProdutoUpdateRequestDtoBuilder() {}

    public static ProdutoUpdateRequestDto criarAjusteDeEstoque(BigDecimal novaQuantidade) {
        return new ProdutoUpdateRequestDto(
                "Pastilha de Freio Atualizada",
                TipoProduto.PECA,
                "Cobreq",
                "C999",
                "Freio dianteiro",
                novaQuantidade,
                null,
                null
        );
    }

    public static ProdutoUpdateRequestDto criarSemAjusteDeEstoque() {
        return new ProdutoUpdateRequestDto(
                "Pastilha de Freio Atualizada",
                TipoProduto.PECA,
                "Cobreq",
                "C999",
                "Freio",
                null,
                new BigDecimal("55.00"),
                new BigDecimal("130.00")
        );
    }
}
