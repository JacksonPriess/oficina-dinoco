package com.dinoco.oficina.cliente.application.usecases.commands.criar;

public interface CriarClienteUseCase {
    CriarClienteOutput executar(CriarClienteCommand input);
}