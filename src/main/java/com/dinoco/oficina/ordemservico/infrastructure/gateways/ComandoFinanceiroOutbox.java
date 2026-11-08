package com.dinoco.oficina.ordemservico.infrastructure.gateways;

import com.dinoco.oficina.ordemservico.application.gateways.ComandoFinanceiroGateway;
import com.dinoco.oficina.ordemservico.domain.models.SagaPagamento;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;
import java.util.UUID;

@Component
@Transactional(propagation = Propagation.MANDATORY)
public class ComandoFinanceiroOutbox implements ComandoFinanceiroGateway {

    private final JdbcTemplate jdbc;
    private final JsonMapper json;

    public ComandoFinanceiroOutbox(JdbcTemplate jdbc, JsonMapper json) {
        this.jdbc = jdbc;
        this.json = json;
    }

    @Override
    public void criarPagamento(SagaPagamento saga) {
        registrar(saga, "pagamento.criar.v1", Map.of(
                "sagaId", saga.sagaId(), "osId", saga.osId(), "valor", saga.valor(), "moeda", "BRL"));
    }

    @Override
    public void estornarPagamento(SagaPagamento saga) {
        registrar(saga, "pagamento.estornar.v1", Map.of("sagaId", saga.sagaId()));
    }

    private void registrar(SagaPagamento saga, String tipo, Map<String, ?> payload) {
        jdbc.update("INSERT INTO os_pagamento_outbox(event_id,saga_id,tipo,payload) VALUES (?,?,?,?)",
                UUID.randomUUID(), saga.sagaId(), tipo, json.writeValueAsString(payload));
    }
}
