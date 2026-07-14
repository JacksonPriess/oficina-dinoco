package com.dinoco.oficina.cliente.infrastructure.config;

import com.dinoco.oficina.cliente.adapters.controllers.ClienteControllerClean;
import com.dinoco.oficina.cliente.application.gateways.ClienteCommandGateway;
import com.dinoco.oficina.cliente.application.gateways.ClienteQueryGateway;
import com.dinoco.oficina.cliente.application.usecases.commands.atualizar.AtualizarClienteHandler;
import com.dinoco.oficina.cliente.application.usecases.commands.atualizar.AtualizarClienteUseCase;
import com.dinoco.oficina.cliente.application.usecases.commands.criar.CriarClienteHandler;
import com.dinoco.oficina.cliente.application.usecases.commands.criar.CriarClienteUseCase;
import com.dinoco.oficina.cliente.application.usecases.commands.desativar.DesativarClienteHandler;
import com.dinoco.oficina.cliente.application.usecases.commands.desativar.DesativarClienteUseCase;
import com.dinoco.oficina.cliente.application.usecases.queries.buscarporid.BuscarClientePorIdHandler;
import com.dinoco.oficina.cliente.application.usecases.queries.buscarporid.BuscarClientePorIdUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClienteConfig {

    @Bean
    public CriarClienteUseCase criarClienteUseCase(ClienteCommandGateway clienteCommandGateway, ClienteQueryGateway clienteQueryGateway) {
        return new CriarClienteHandler(clienteCommandGateway, clienteQueryGateway);
    }

    @Bean
    public AtualizarClienteUseCase atualizarClienteUseCase(ClienteCommandGateway commandGateway) {
        return new AtualizarClienteHandler(commandGateway);
    }

    @Bean
    public DesativarClienteUseCase desativarClienteUseCase(ClienteCommandGateway commandGateway) {
        return new DesativarClienteHandler(commandGateway);
    }

    @Bean
    public BuscarClientePorIdUseCase buscarClientePorIdUseCase(ClienteQueryGateway queryGateway) {
        return new BuscarClientePorIdHandler(queryGateway);
    }

    @Bean
    public ClienteControllerClean clienteControllerClean(
            CriarClienteUseCase criarClienteUseCase,
            AtualizarClienteUseCase atualizarClienteUseCase,
            DesativarClienteUseCase desativarClienteUseCase,
            BuscarClientePorIdUseCase buscarClientePorIdUseCase) {
        return new ClienteControllerClean(criarClienteUseCase, atualizarClienteUseCase, desativarClienteUseCase, buscarClientePorIdUseCase);
    }
}
