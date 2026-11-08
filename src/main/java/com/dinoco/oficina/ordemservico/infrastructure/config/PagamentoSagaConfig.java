package com.dinoco.oficina.ordemservico.infrastructure.config;

import com.dinoco.oficina.ordemservico.application.gateways.ComandoFinanceiroGateway;
import com.dinoco.oficina.ordemservico.application.gateways.SagaPagamentoGateway;
import com.dinoco.oficina.ordemservico.application.saga.OrquestradorPagamento;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class PagamentoSagaConfig {

    @Bean
    public OrquestradorPagamento orquestradorPagamento(SagaPagamentoGateway sagas, ComandoFinanceiroGateway comandos) {
        return new OrquestradorPagamento(sagas, comandos);
    }
}
