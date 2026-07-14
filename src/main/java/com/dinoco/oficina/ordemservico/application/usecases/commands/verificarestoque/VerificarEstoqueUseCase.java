package com.dinoco.oficina.ordemservico.application.usecases.commands.verificarestoque;

public interface VerificarEstoqueUseCase {
    VerificarEstoqueOutput executar(VerificarEstoqueCommand command);
}
