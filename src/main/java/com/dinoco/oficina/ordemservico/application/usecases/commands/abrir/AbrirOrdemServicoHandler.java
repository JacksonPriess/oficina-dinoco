package com.dinoco.oficina.ordemservico.application.usecases.commands.abrir;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import com.dinoco.oficina.cliente.application.gateways.ClienteQueryGateway;

public class AbrirOrdemServicoHandler implements AbrirOrdemServicoUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;
    private final ClienteQueryGateway clienteQueryGateway;

    public AbrirOrdemServicoHandler(OrdemServicoCommandGateway ordemServicoCommandGateway, ClienteQueryGateway clienteQueryGateway) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
        this.clienteQueryGateway = clienteQueryGateway;
    }

    public AbrirOrdemServicoOutput executar(AbrirOrdemServicoCommand command) {

        OrdemServico novaOs = new OrdemServico(
                command.clienteId(),
                command.veiculoId(),
                command.quilometragemEntrada(),
                command.reclamacaoCliente()
        );

        OrdemServico osSalva = ordemServicoCommandGateway.salvar(novaOs);

        return new AbrirOrdemServicoOutput(
                osSalva.getId(),
                osSalva.getCodigoRastreio(),
                osSalva.getClienteId(),
                osSalva.getVeiculoId(),
                osSalva.getStatus(),
                osSalva.getReclamacaoCliente(),
                osSalva.getQuilometragemEntrada()
        );
    }
}