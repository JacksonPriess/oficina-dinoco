package com.dinoco.oficina.veiculo.adapters.controllers;

import com.dinoco.oficina.veiculo.application.usecases.commands.atualizar.AtualizarVeiculoCommand;
import com.dinoco.oficina.veiculo.application.usecases.commands.atualizar.AtualizarVeiculoOutput;
import com.dinoco.oficina.veiculo.application.usecases.commands.atualizar.AtualizarVeiculoUseCase;
import com.dinoco.oficina.veiculo.application.usecases.commands.criar.CriarVeiculoCommand;
import com.dinoco.oficina.veiculo.application.usecases.commands.criar.CriarVeiculoOutput;
import com.dinoco.oficina.veiculo.application.usecases.commands.criar.CriarVeiculoUseCase;
import com.dinoco.oficina.veiculo.application.usecases.commands.desativar.DesativarVeiculoCommand;
import com.dinoco.oficina.veiculo.application.usecases.commands.desativar.DesativarVeiculoUseCase;
import com.dinoco.oficina.veiculo.application.usecases.queries.buscarporid.BuscarVeiculoPorIdOutput;
import com.dinoco.oficina.veiculo.application.usecases.queries.buscarporid.BuscarVeiculoPorIdQuery;
import com.dinoco.oficina.veiculo.application.usecases.queries.buscarporid.BuscarVeiculoPorIdUseCase;

/**
 * Orquestra commands e queries
 */
public class VeiculoControllerClean {

    private final CriarVeiculoUseCase criarVeiculoUseCase;
    private final AtualizarVeiculoUseCase atualizarVeiculoUseCase;
    private final DesativarVeiculoUseCase desativarVeiculoUseCase;
    private final BuscarVeiculoPorIdUseCase buscarVeiculoPorIdUseCase;

    public VeiculoControllerClean(CriarVeiculoUseCase criarVeiculoUseCase, AtualizarVeiculoUseCase atualizarVeiculoUseCase, DesativarVeiculoUseCase desativarVeiculoUseCase, BuscarVeiculoPorIdUseCase buscarVeiculoPorIdUseCase) {
        this.criarVeiculoUseCase = criarVeiculoUseCase;
        this.atualizarVeiculoUseCase = atualizarVeiculoUseCase;
        this.desativarVeiculoUseCase = desativarVeiculoUseCase;
        this.buscarVeiculoPorIdUseCase = buscarVeiculoPorIdUseCase;
    }

    public CriarVeiculoOutput criarVeiculo(CriarVeiculoCommand command) {
        return criarVeiculoUseCase.executar(command);
    }

    public AtualizarVeiculoOutput atualizarVeiculo(AtualizarVeiculoCommand command) {
        return atualizarVeiculoUseCase.executar(command);
    }

    public void desativarVeiculo(DesativarVeiculoCommand command) {
        desativarVeiculoUseCase.executar(command);
    }

    public BuscarVeiculoPorIdOutput buscarPorId(BuscarVeiculoPorIdQuery query) {
        return buscarVeiculoPorIdUseCase.executar(query);
    }
}