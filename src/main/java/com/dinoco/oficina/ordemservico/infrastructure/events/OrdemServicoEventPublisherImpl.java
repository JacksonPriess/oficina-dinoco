package com.dinoco.oficina.ordemservico.infrastructure.events;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoEventPublisher;
import com.dinoco.oficina.shared.events.ExecucaoIniciadaEvent;
import com.dinoco.oficina.shared.events.OrcamentoAprovadoEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class OrdemServicoEventPublisherImpl implements OrdemServicoEventPublisher {

    private final ApplicationEventPublisher springPublisher;

    public OrdemServicoEventPublisherImpl(ApplicationEventPublisher springPublisher) {
        this.springPublisher = springPublisher;
    }

    @Override
    public void publicarOrcamentoAprovado(OrcamentoAprovadoEvent event) {
        springPublisher.publishEvent(event);
    }

    @Override
    public void publicarExecucaoIniciada(ExecucaoIniciadaEvent event){
        springPublisher.publishEvent(event);
    }


}