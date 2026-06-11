package com.dinoco.oficina.veiculo.application.usecases.commands.criar;

import com.dinoco.oficina.veiculo.application.gateways.VeiculoCommandGateway;
import com.dinoco.oficina.veiculo.application.gateways.VeiculoQueryGateway;
import com.dinoco.oficina.veiculo.domain.Veiculo;

public class CriarVeiculoHandler implements CriarVeiculoUseCase {

    private final VeiculoCommandGateway veiculoCommandGateway;
    private final VeiculoQueryGateway veiculoQueryGateway;

    public CriarVeiculoHandler(VeiculoCommandGateway veiculoCommandGateway, VeiculoQueryGateway veiculoQueryGateway) {
        this.veiculoCommandGateway = veiculoCommandGateway;
        this.veiculoQueryGateway = veiculoQueryGateway;
    }

    @Override
    public CriarVeiculoOutput executar(CriarVeiculoCommand command) {

        if (command.placa() == null || command.placa().trim().isEmpty()) {
            throw new IllegalArgumentException("A placa não pode estar vazia.");
        }

        if (veiculoQueryGateway.existePorPlaca(command.placa())) {
            throw new IllegalArgumentException("Veiculo já cadastrado com esta placa.");
        }

        Veiculo novoVeiculo = new Veiculo(
                command.placa(),
                command.marca(),
                command.modelo(),
                command.anoFabricacao(),
                command.anoModelo(),
                command.cor(),
                command.chassi(),
                command.motor()
        );

        Veiculo veiculoSalvo = veiculoCommandGateway.salvar(novoVeiculo);

        return mapearParaOutput(veiculoSalvo);
    }

    private CriarVeiculoOutput mapearParaOutput(Veiculo veiculo) {

        return new CriarVeiculoOutput(
                veiculo.getId(),
                veiculo.getPlaca(),
                veiculo.getMarca(),
                veiculo.getModelo(),
                veiculo.getAnoFabricacao(),
                veiculo.getAnoModelo(),
                veiculo.getCor(),
                veiculo.getChassi(),
                veiculo.getMotor(),
                veiculo.getAtivo()
        );
    }
}