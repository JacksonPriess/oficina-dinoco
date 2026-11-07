package com.dinoco.oficina.ordemservico.infrastructure.events;

import com.dinoco.oficina.ordemservico.application.saga.EventoFinanceiro;
import com.dinoco.oficina.ordemservico.application.saga.OrquestradorPagamento;
import com.dinoco.oficina.ordemservico.infrastructure.config.FinanceiroRabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.json.JsonMapper;

@Component
@Profile("rabbit")
@Slf4j
public class EventoFinanceiroListener {

    private final OrquestradorPagamento orquestrador;
    private final JsonMapper json;
    private final TransactionTemplate tx;

    public EventoFinanceiroListener(OrquestradorPagamento orquestrador, JsonMapper json,
                                    PlatformTransactionManager manager) {
        this.orquestrador = orquestrador;
        this.json = json;
        this.tx = new TransactionTemplate(manager);
    }

    @RabbitListener(queues = FinanceiroRabbitConfig.EVENTOS)
    public void receber(Message message) {
        var evento = json.readValue(message.getBody(), EventoFinanceiro.class);
        if (evento.tipo() == null || !evento.tipo().equals(message.getMessageProperties().getReceivedRoutingKey())) {
            throw new IllegalArgumentException("Tipo do evento financeiro incompatível com a chave de roteamento.");
        }
        var saga = tx.execute(status -> orquestrador.receber(evento));
        log.info("evento=os_pagamento_resultado_processado eventoId={} sagaId={} osId={} estado={}",
                evento.eventId(), saga.sagaId(), saga.osId(), saga.estado());
    }
}
