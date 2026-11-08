package com.dinoco.oficina.ordemservico.infrastructure.gateways;

import com.dinoco.oficina.ordemservico.application.gateways.SagaPagamentoGateway;
import com.dinoco.oficina.ordemservico.domain.models.SagaPagamento;
import com.dinoco.oficina.ordemservico.infrastructure.persistence.SagaPagamentoEntity;
import com.dinoco.oficina.ordemservico.infrastructure.persistence.SagaPagamentoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SagaPagamentoGatewayImpl implements SagaPagamentoGateway {

    private final SagaPagamentoJpaRepository repository;

    public SagaPagamentoGatewayImpl(SagaPagamentoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<SagaPagamento> buscarPorOsId(Long osId) {
        return repository.findByOsId(osId).map(this::mapearParaDominio);
    }

    @Override
    public Optional<SagaPagamento> bloquearPorOsId(Long osId) {
        return repository.bloquearPorOsId(osId).map(this::mapearParaDominio);
    }

    @Override
    public void salvar(SagaPagamento saga) {
        repository.saveAndFlush(new SagaPagamentoEntity(saga.sagaId(), saga.osId(), saga.valor(), saga.estado(),
                saga.pagamentoId(), saga.provedorId(), saga.estornoId()));
    }

    private SagaPagamento mapearParaDominio(SagaPagamentoEntity entity) {
        return new SagaPagamento(entity.getSagaId(), entity.getOsId(), entity.getValor(), entity.getEstado(),
                entity.getPagamentoId(), entity.getProvedorId(), entity.getEstornoId());
    }
}
