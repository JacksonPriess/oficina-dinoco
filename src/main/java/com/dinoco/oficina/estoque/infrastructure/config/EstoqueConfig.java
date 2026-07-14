package com.dinoco.oficina.estoque.infrastructure.config;

import com.dinoco.oficina.estoque.adapters.controllers.EstoqueControllerClean;
import com.dinoco.oficina.estoque.application.gateways.EstoqueCommandGateway;
import com.dinoco.oficina.estoque.application.usecases.commands.ajustarinventario.AjustarInventarioHandler;
import com.dinoco.oficina.estoque.application.usecases.commands.ajustarinventario.AjustarInventarioUseCase;
import com.dinoco.oficina.estoque.application.usecases.commands.registrarentrada.RegistrarEntradaHandler;
import com.dinoco.oficina.estoque.application.usecases.commands.registrarentrada.RegistrarEntradaUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EstoqueConfig {


    @Bean
    public EstoqueControllerClean estoqueControllerClean(AjustarInventarioUseCase ajustarInventarioUseCase,
                                                         RegistrarEntradaUseCase registrarEntradaUseCase) {
        return new EstoqueControllerClean(ajustarInventarioUseCase, registrarEntradaUseCase);
    }

    @Bean
    public AjustarInventarioUseCase ajustarInventarioUseCase(EstoqueCommandGateway commandGateway) {
        return new AjustarInventarioHandler(commandGateway);
    }

    @Bean
    public RegistrarEntradaUseCase registrarEntradaUseCase(EstoqueCommandGateway commandGateway) {
        return new RegistrarEntradaHandler(commandGateway);
    }
}
