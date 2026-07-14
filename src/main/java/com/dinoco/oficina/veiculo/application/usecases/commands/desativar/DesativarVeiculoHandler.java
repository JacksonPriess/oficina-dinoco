package com.dinoco.oficina.veiculo.application.usecases.commands.desativar;

import com.dinoco.oficina.veiculo.application.gateways.VeiculoCommandGateway;
import com.dinoco.oficina.veiculo.domain.Veiculo;

public class DesativarVeiculoHandler implements DesativarVeiculoUseCase {

    private final VeiculoCommandGateway veiculoCommandGateway;

    public DesativarVeiculoHandler(VeiculoCommandGateway veiculoCommandGateway) {
        this.veiculoCommandGateway = veiculoCommandGateway;
    }

    @Override
    public void executar(DesativarVeiculoCommand command) {

        Veiculo veiculo = veiculoCommandGateway.buscarParaAlteracao(command.id())
                .orElseThrow(() -> new IllegalArgumentException("Veiculo não encontrado."));

        veiculo.desativar();

        veiculoCommandGateway.salvar(veiculo);
    }
}
