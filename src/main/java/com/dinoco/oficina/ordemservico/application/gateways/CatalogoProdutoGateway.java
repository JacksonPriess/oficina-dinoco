package com.dinoco.oficina.ordemservico.application.gateways;

import java.math.BigDecimal;

public interface CatalogoProdutoGateway {
    BigDecimal buscarPrecoVendaAtual(Long produtoId);
}
