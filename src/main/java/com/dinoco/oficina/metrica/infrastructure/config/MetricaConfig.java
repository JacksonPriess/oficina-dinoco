package com.dinoco.oficina.metrica.infrastructure.config;

import com.dinoco.oficina.metrica.adapters.controllers.MetricaControllerClean;
import com.dinoco.oficina.metrica.application.gateways.MetricaQueryGateway;
import com.dinoco.oficina.metrica.application.usecase.queries.buscarmediaexecucao.BuscarMediaExecucaoServicosHandler;
import com.dinoco.oficina.metrica.application.usecase.queries.buscarmediaexecucao.BuscarMediaExecucaoServicosUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricaConfig {

    @Bean
    public MetricaControllerClean metricaControllerClean(BuscarMediaExecucaoServicosUseCase buscarMediaExecucaoServicosUseCase) {
        return new MetricaControllerClean(buscarMediaExecucaoServicosUseCase);
    }

    @Bean
    public BuscarMediaExecucaoServicosUseCase buscarMediaExecucaoServicosUseCase(MetricaQueryGateway queryGateway) {
        return new BuscarMediaExecucaoServicosHandler(queryGateway);
    }
}