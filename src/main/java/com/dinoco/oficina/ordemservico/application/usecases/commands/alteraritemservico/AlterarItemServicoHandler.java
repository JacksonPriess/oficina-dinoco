package com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemservico;

import com.dinoco.oficina.ordemservico.application.gateways.FuncionarioGateway;
import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;

public class AlterarItemServicoHandler implements AlterarItemServicoUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;
    private final FuncionarioGateway funcionarioGateway;

    public AlterarItemServicoHandler(OrdemServicoCommandGateway ordemServicoCommandGateway, FuncionarioGateway funcionarioGateway) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
        this.funcionarioGateway = funcionarioGateway;
    }

    @Override
    public void executar(AlterarItemServicoCommand command) {
        // 1. Busca a Ordem de Serviço inteira (O Agregado Raiz)
        OrdemServico ordemServico = ordemServicoCommandGateway.buscarParaAlteracao(command.osId())
                .orElseThrow(() -> new IllegalArgumentException("OS não encontrada."));

        if (command.mecanicoId() != null) {
            boolean mecanicoExiste = funcionarioGateway.existeMecanicoAtivo(command.mecanicoId());
            if (!mecanicoExiste) {
                throw new IllegalArgumentException("Mecânico não encontrado ou inativo.");
            }
        }
        ordemServico.alterarItemServico(command.itemId(), command.valorCobrado(), command.mecanicoId());
        ordemServicoCommandGateway.salvar(ordemServico);
    }
}