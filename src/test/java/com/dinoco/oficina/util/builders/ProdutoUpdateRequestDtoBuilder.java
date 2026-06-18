package com.dinoco.oficina.util.builders;


import com.dinoco.oficina.catalogoproduto.domain.TipoProduto;
import com.dinoco.oficina.catalogoproduto.infrastructure.web.dto.ProdutoUpdateRequestDto;

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
                "Freio dianteiro",
                null,
                null,
                null
        );
    }
}
