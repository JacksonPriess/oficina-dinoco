package com.dinoco.oficina.estoque.infrastructure.web.dto;

import java.math.BigDecimal;

public record BuscarEstoquePorProdutoIdResponseDto( Long id,
                                                    Long produtoId,
                                                    BigDecimal quantidadeReal,
                                                    BigDecimal quantidadeReservada) {

}
