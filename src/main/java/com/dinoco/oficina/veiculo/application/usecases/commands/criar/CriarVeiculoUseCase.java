package com.dinoco.oficina.veiculo.application.usecases.commands.criar;

public interface CriarVeiculoUseCase {
    CriarVeiculoOutput executar(CriarVeiculoCommand input);
}