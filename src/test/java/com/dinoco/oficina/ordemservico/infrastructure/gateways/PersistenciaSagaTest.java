package com.dinoco.oficina.ordemservico.infrastructure.gateways;

import com.dinoco.oficina.ordemservico.domain.models.SagaPagamento;
import com.dinoco.oficina.ordemservico.infrastructure.persistence.*;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.mockito.ArgumentCaptor;
import tools.jackson.databind.json.JsonMapper;
import java.math.BigDecimal;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersistenciaSagaTest {
    @Test void salvarERecuperarSagaPreservaIdentificadoresEEstado() {
        var repository = mock(SagaPagamentoJpaRepository.class);
        var gateway = new SagaPagamentoGatewayImpl(repository);
        var saga = new SagaPagamento(UUID.randomUUID(), 42L, new BigDecimal("250.50"), SagaPagamento.Estado.ESTORNADA,
                UUID.randomUUID(), "provedor-1", "estorno-2");
        var captor = ArgumentCaptor.forClass(SagaPagamentoEntity.class);
        gateway.salvar(saga);
        verify(repository).saveAndFlush(captor.capture());
        when(repository.findByOsId(42L)).thenReturn(Optional.of(captor.getValue()));
        when(repository.bloquearPorOsId(42L)).thenReturn(Optional.of(captor.getValue()));
        assertThat(gateway.buscarPorOsId(42L)).contains(saga);
        assertThat(gateway.bloquearPorOsId(42L)).contains(saga);
        assertThat(gateway.buscarPorOsId(99L)).isEmpty();
    }
    @Test void criarEEstornarRegistramContratosDistintosNaOutbox() {
        var jdbc = mock(JdbcTemplate.class);
        var json = JsonMapper.builder().build();
        var gateway = new ComandoFinanceiroOutbox(jdbc, json);
        var saga = SagaPagamento.iniciar(42L, new BigDecimal("250.50"));
        gateway.criarPagamento(saga);
        gateway.estornarPagamento(saga);
        var tipos = ArgumentCaptor.forClass(String.class);
        var payloads = ArgumentCaptor.forClass(String.class);
        verify(jdbc, times(2)).update(contains("INSERT INTO os_pagamento_outbox"), any(UUID.class), eq(saga.sagaId()), tipos.capture(), payloads.capture());
        assertThat(tipos.getAllValues()).containsExactly("pagamento.criar.v1", "pagamento.estornar.v1");
        var criar = json.readTree(payloads.getAllValues().getFirst());
        assertThat(criar.get("sagaId").asString()).isEqualTo(saga.sagaId().toString());
        assertThat(criar.get("osId").asLong()).isEqualTo(42L);
        assertThat(criar.get("moeda").asString()).isEqualTo("BRL");
        assertThat(criar.get("valor").decimalValue()).isEqualByComparingTo("250.50");
        assertThat(json.readTree(payloads.getAllValues().get(1)).get("sagaId").asString()).isEqualTo(saga.sagaId().toString());
    }
}
