package com.dinoco.oficina.estoque.adapters.controllers;

import com.dinoco.oficina.estoque.application.usecases.commands.ajustarinventario.AjustarInventarioCommand;
import com.dinoco.oficina.estoque.application.usecases.commands.ajustarinventario.AjustarInventarioUseCase;
import com.dinoco.oficina.estoque.application.usecases.commands.registrarentrada.RegistrarEntradaCommand;
import com.dinoco.oficina.estoque.application.usecases.commands.registrarentrada.RegistrarEntradaUseCase;

public class EstoqueControllerClean {

    private final AjustarInventarioUseCase ajustarInventarioUseCase;
    private final RegistrarEntradaUseCase registrarEntradaUseCase;

    public EstoqueControllerClean(AjustarInventarioUseCase ajustarInventarioUseCase,
                                  RegistrarEntradaUseCase registrarEntradaUseCase) {
        this.ajustarInventarioUseCase = ajustarInventarioUseCase;
        this.registrarEntradaUseCase = registrarEntradaUseCase;
    }

    public void ajustarInventario(AjustarInventarioCommand command) {
        ajustarInventarioUseCase.executar(command);
    }

    public void registrarEntrada(RegistrarEntradaCommand command) {
        registrarEntradaUseCase.executar(command);
    }

}
