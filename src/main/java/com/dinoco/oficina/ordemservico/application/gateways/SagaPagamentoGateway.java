package com.dinoco.oficina.ordemservico.application.gateways;

import com.dinoco.oficina.ordemservico.domain.models.SagaPagamento;

import java.util.Optional;

public interface SagaPagamentoGateway {
    Optional<SagaPagamento> buscarPorOsId(Long osId);
    Optional<SagaPagamento> bloquearPorOsId(Long osId);
    void salvar(SagaPagamento saga);
}
