package com.dinoco.oficina.estoque.adapters.controllers;

import com.dinoco.oficina.estoque.application.usecases.commands.ajustarinventario.AjustarInventarioCommand;
import com.dinoco.oficina.estoque.application.usecases.commands.ajustarinventario.AjustarInventarioUseCase;

public class EstoqueControllerClean {

    private final AjustarInventarioUseCase ajustarInventarioUseCase;

    public EstoqueControllerClean(AjustarInventarioUseCase ajustarInventarioUseCase) {
        this.ajustarInventarioUseCase = ajustarInventarioUseCase;
    }

    public void ajustarInventario(AjustarInventarioCommand command) {
        ajustarInventarioUseCase.executar(command);
    }
}
