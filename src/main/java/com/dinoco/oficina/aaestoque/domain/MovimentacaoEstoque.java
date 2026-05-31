package com.dinoco.oficina.aaestoque.domain;

import com.dinoco.oficina.catalogoproduto.domain.Produto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimentacaoEstoque {

    private Long id;
    private Produto produto;
    private TipoMovimentacao tipoMovimentacao;
    private BigDecimal quantidade;
    private LocalDateTime dataMovimentacao;
    private String observacao;

}
