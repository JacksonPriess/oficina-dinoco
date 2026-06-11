package com.dinoco.oficina.estoque.domain;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MovimentacaoEstoque {

    private Long produtoId;
    private TipoMovimentacao tipo;
    private BigDecimal quantidade;
    private LocalDateTime dataMovimentacao;
    private String observacao;

    public MovimentacaoEstoque(Long produtoId, TipoMovimentacao tipo, BigDecimal quantidade, String observacao) {
        this.produtoId = produtoId;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.dataMovimentacao = LocalDateTime.now();
        this.observacao = observacao;
    }

}
