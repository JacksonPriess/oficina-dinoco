package com.dinoco.oficina.cliente.adapters.controllers;

import com.dinoco.oficina.cliente.application.usecases.commands.atualizar.AtualizarClienteCommand;
import com.dinoco.oficina.cliente.application.usecases.commands.atualizar.AtualizarClienteOutput;
import com.dinoco.oficina.cliente.application.usecases.commands.atualizar.AtualizarClienteUseCase;
import com.dinoco.oficina.cliente.application.usecases.commands.criar.CriarClienteUseCase;
import com.dinoco.oficina.cliente.application.usecases.commands.criar.CriarClienteCommand;
import com.dinoco.oficina.cliente.application.usecases.commands.criar.CriarClienteOutput;
import com.dinoco.oficina.cliente.application.usecases.commands.desativar.DesativarClienteCommand;
import com.dinoco.oficina.cliente.application.usecases.commands.desativar.DesativarClienteUseCase;
import com.dinoco.oficina.cliente.application.usecases.queries.buscarporid.BuscarClientePorIdOutput;
import com.dinoco.oficina.cliente.application.usecases.queries.buscarporid.BuscarClientePorIdQuery;
import com.dinoco.oficina.cliente.application.usecases.queries.buscarporid.BuscarClientePorIdUseCase;

/**
 * Orquestra commands e queries
 */
public class ClienteControllerClean {

    private final CriarClienteUseCase criarClienteUseCase;
    private final AtualizarClienteUseCase atualizarClienteUseCase;
    private final DesativarClienteUseCase desativarClienteUseCase;
    private final BuscarClientePorIdUseCase buscarClientePorIdUseCase;

    public ClienteControllerClean(CriarClienteUseCase criarClienteUseCase, AtualizarClienteUseCase atualizarClienteUseCase, DesativarClienteUseCase desativarClienteUseCase, BuscarClientePorIdUseCase buscarClientePorIdUseCase) {
        this.criarClienteUseCase = criarClienteUseCase;
        this.atualizarClienteUseCase = atualizarClienteUseCase;
        this.desativarClienteUseCase = desativarClienteUseCase;
        this.buscarClientePorIdUseCase = buscarClientePorIdUseCase;
    }

    public CriarClienteOutput criarCliente(CriarClienteCommand command) {
        return criarClienteUseCase.executar(command);
    }

    public AtualizarClienteOutput atualizarCliente(AtualizarClienteCommand command) {
        return atualizarClienteUseCase.executar(command);
    }

    public void desativarCliente(DesativarClienteCommand command) {
        desativarClienteUseCase.executar(command);
    }

    public BuscarClientePorIdOutput buscarPorId(BuscarClientePorIdQuery query) {
        return buscarClientePorIdUseCase.executar(query);
    }
}