package com.dinoco.oficina.estoque.adapters.controllers;

import com.dinoco.oficina.estoque.application.usecases.commands.ajustarinventario.AjustarInventarioCommand;
import com.dinoco.oficina.estoque.application.usecases.commands.ajustarinventario.AjustarInventarioUseCase;
import com.dinoco.oficina.estoque.application.usecases.commands.registrarentrada.RegistrarEntradaCommand;
import com.dinoco.oficina.estoque.application.usecases.commands.registrarentrada.RegistrarEntradaUseCase;
import com.dinoco.oficina.estoque.application.usecases.queries.buscarporid.BuscarEstoquePorIdOuput;
import com.dinoco.oficina.estoque.application.usecases.queries.buscarporid.BuscarEstoquePorIdQuery;
import com.dinoco.oficina.estoque.application.usecases.queries.buscarporid.BuscarEstoquePorIdUseCase;

public class EstoqueControllerClean {

    private final AjustarInventarioUseCase ajustarInventarioUseCase;
    private final RegistrarEntradaUseCase registrarEntradaUseCase;
    private final BuscarEstoquePorIdUseCase buscarEstoquePorIdUseCase;

    public EstoqueControllerClean(AjustarInventarioUseCase ajustarInventarioUseCase,
                                  RegistrarEntradaUseCase registrarEntradaUseCase,
                                  BuscarEstoquePorIdUseCase buscarEstoquePorIdUseCase) {
        this.ajustarInventarioUseCase = ajustarInventarioUseCase;
        this.registrarEntradaUseCase = registrarEntradaUseCase;
        this.buscarEstoquePorIdUseCase = buscarEstoquePorIdUseCase;
    }

    public void ajustarInventario(AjustarInventarioCommand command) {
        ajustarInventarioUseCase.executar(command);
    }

    public void registrarEntrada(RegistrarEntradaCommand command) {
        registrarEntradaUseCase.executar(command);
    }

    public BuscarEstoquePorIdOuput buscarEstoquePorIdOuput(BuscarEstoquePorIdQuery query) {
        return buscarEstoquePorIdUseCase.executar(query);
    }
}
