package com.dinoco.oficina.ordemservico.application.usecases.commands.abrir;

import java.util.List;

public record AbrirOrdemServicoCommand(
        Long clienteId,
        Long veiculoId,
        Integer quilometragemEntrada,
        String reclamacaoCliente,
        List<AbrirOrdemServicoItemProdutoCommand> produtos,
        List<AbrirOrdemServicoItemServicoCommand> servicos
) {}