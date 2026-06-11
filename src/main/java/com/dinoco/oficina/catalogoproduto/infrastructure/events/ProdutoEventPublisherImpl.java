package com.dinoco.oficina.catalogoproduto.infrastructure.events;

import com.dinoco.oficina.catalogoproduto.application.gateways.ProdutoEventPublisher;
import com.dinoco.oficina.shared.events.ProdutoCadastradoEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ProdutoEventPublisherImpl implements ProdutoEventPublisher {

    private final ApplicationEventPublisher springPublisher;

    public ProdutoEventPublisherImpl(ApplicationEventPublisher springPublisher) {
        this.springPublisher = springPublisher;
    }

    @Override
    public void publicar(ProdutoCadastradoEvent event) {
        springPublisher.publishEvent(event);
    }
}