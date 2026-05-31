package com.dinoco.oficina.util.builders;

import com.dinoco.oficina.catalogoproduto.domain.TipoProduto;
import com.dinoco.oficina.entity.Produto;

import java.math.BigDecimal;

public class ProdutoBuilder {
    private Produto produto;

    private ProdutoBuilder() {
        this.produto = new Produto();
    }

    public static ProdutoBuilder umProduto() {
        ProdutoBuilder builder = new ProdutoBuilder();
        builder.produto.setId(1L);
        builder.produto.setNome("Filtro de Óleo");
        builder.produto.setTipo(TipoProduto.PECA); // Assumindo seu Enum
        builder.produto.setMarca("Bosch");
        builder.produto.setCodigoFabricante("B12345");
        builder.produto.setAplicacao("Gol, Fox, Voyage");
        builder.produto.setQuantidadeAtual(BigDecimal.ZERO);
        builder.produto.setQuantidadeReservada(BigDecimal.ZERO);
        builder.produto.setPrecoCusto(new BigDecimal("20.00"));
        builder.produto.setPrecoVenda(new BigDecimal("45.00"));
        builder.produto.setAtivo(true);
        return builder;
    }

    public ProdutoBuilder comId(Long id) {
        this.produto.setId(id);
        return this;
    }

    public ProdutoBuilder comQuantidadeAtual(BigDecimal quantidade) {
        this.produto.setQuantidadeAtual(quantidade);
        return this;
    }

    public ProdutoBuilder comPrecoVenda(BigDecimal preco) {
        this.produto.setPrecoVenda(preco);
        return this;
    }

    public Produto build() {
        return this.produto;
    }
}
