package com.dinoco.oficina.ordemservico.application.gateways;

import com.dinoco.oficina.ordemservico.domain.models.SagaPagamento;

public interface ComandoFinanceiroGateway {
    void criarPagamento(SagaPagamento saga);
    void estornarPagamento(SagaPagamento saga);
}
