package com.dinoco.oficina.veiculo.application.usecases.commands.atualizar;

import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.veiculo.application.gateways.VeiculoCommandGateway;
import com.dinoco.oficina.veiculo.domain.Veiculo;

public class AtualizarVeiculoHandler implements AtualizarVeiculoUseCase {

    private final VeiculoCommandGateway veiculoCommandGateway;

    public AtualizarVeiculoHandler(VeiculoCommandGateway veiculoCommandGateway) {
        this.veiculoCommandGateway = veiculoCommandGateway;
    }

    @Override
    public AtualizarVeiculoOutput executar(AtualizarVeiculoCommand command) {

        Veiculo veiculo = veiculoCommandGateway.buscarParaAlteracao(command.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veiculo não encontrado."));

        veiculo.atualizar(
                command.placa(),
                command.marca(),
                command.modelo(),
                command.anoFabricacao(),
                command.anoModelo(),
                command.cor(),
                command.chassi(),
                command.motor()
        );

        Veiculo veiculoSalvo = veiculoCommandGateway.salvar(veiculo);

        return mapearParaOutput(veiculoSalvo);
    }

    private AtualizarVeiculoOutput mapearParaOutput(Veiculo veiculo) {

        return new AtualizarVeiculoOutput(
                veiculo.getId(),
                veiculo.getPlaca(),
                veiculo.getMarca(),
                veiculo.getModelo(),
                veiculo.getAnoFabricacao(),
                veiculo.getAnoModelo(),
                veiculo.getCor(),
                veiculo.getChassi(),
                veiculo.getMotor()
        );
    }
}
