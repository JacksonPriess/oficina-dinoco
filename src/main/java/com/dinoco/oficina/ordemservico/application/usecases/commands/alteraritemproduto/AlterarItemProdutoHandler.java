package com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemproduto;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;

public class AlterarItemProdutoHandler implements AlterarItemProdutoUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;

    public AlterarItemProdutoHandler(OrdemServicoCommandGateway ordemServicoCommandGateway) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
    }

    @Override
    public void executar(AlterarItemProdutoCommand command) {
        // 1. Busca a Ordem de Serviço inteira (O Agregado Raiz)
        OrdemServico ordemServico = ordemServicoCommandGateway.buscarParaAlteracao(command.osId())
                .orElseThrow(() -> new IllegalArgumentException("OS não encontrada."));

        ordemServico.alterarItemProduto(command.itemId(), command.precoVenda(), command.quantidade());
        ordemServicoCommandGateway.salvar(ordemServico);
    }
}

