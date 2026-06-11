package com.dinoco.oficina.ordemservico.domain.models;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemOSProduto {

    private Long id;
    private Long produtoId;
    private BigDecimal quantidade;
    private BigDecimal valorUnitarioVenda;

    public ItemOSProduto(Long produtoId, BigDecimal quantidade, BigDecimal valorUnitarioVenda) {
        if (quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.valorUnitarioVenda = valorUnitarioVenda; //Pode ser zero
    }

    public ItemOSProduto(Long id, Long produtoId, BigDecimal quantidade, BigDecimal valorUnitarioVenda) {
        this.id = id;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.valorUnitarioVenda = valorUnitarioVenda;
    }

    public void alterarDados(BigDecimal novoValorCobrado, BigDecimal quantidadeUtilizada) {

        if ( novoValorCobrado != null ) {
            if (novoValorCobrado.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("O valor cobrado não pode ser negativo.");
            }
            this.valorUnitarioVenda = novoValorCobrado;
        }

        if ( quantidadeUtilizada != null ) {
            if (quantidade.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
            }
            this.quantidade = quantidadeUtilizada;
        }
    }

    public BigDecimal getValorTotal() {
        return this.quantidade.multiply(this.valorUnitarioVenda);
    }

}
