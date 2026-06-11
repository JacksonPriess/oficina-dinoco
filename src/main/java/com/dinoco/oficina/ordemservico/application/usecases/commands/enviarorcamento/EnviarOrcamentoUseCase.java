package com.dinoco.oficina.ordemservico.application.usecases.commands.enviarorcamento;

public interface EnviarOrcamentoUseCase {
    EnviarOrcamentoOutput executar(EnviarOrcamentoCommand input);
}