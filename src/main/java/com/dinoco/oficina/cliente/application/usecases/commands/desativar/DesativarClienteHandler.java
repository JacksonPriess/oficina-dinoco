package com.dinoco.oficina.cliente.application.usecases.commands.desativar;

import com.dinoco.oficina.cliente.application.gateways.ClienteCommandGateway;
import com.dinoco.oficina.cliente.domain.Cliente;

public class DesativarClienteHandler implements DesativarClienteUseCase {

    private final ClienteCommandGateway clienteCommandGateway;

    public DesativarClienteHandler(ClienteCommandGateway clienteCommandGateway) {
        this.clienteCommandGateway = clienteCommandGateway;
    }

    @Override
    public void executar(DesativarClienteCommand command) {

        Cliente cliente = clienteCommandGateway.buscarParaAlteracao(command.id())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        cliente.desativar();

        clienteCommandGateway.salvar(cliente);
    }
}
