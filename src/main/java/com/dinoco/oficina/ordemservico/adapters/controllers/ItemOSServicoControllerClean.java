package com.dinoco.oficina.ordemservico.adapters.controllers;

import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemservico.AdicionarItemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemservico.AdicionarItemServicoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemservico.AlterarItemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemservico.AlterarItemServicoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirexecucaoitemservico.ConcluirExecucaoItemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirexecucaoitemservico.ConcluirExecucaoItemServicoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucaoitemservico.IniciarExecucaoItemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucaoitemservico.IniciarExecucaoItemServicoUseCase;

public class ItemOSServicoControllerClean {

    private final AdicionarItemServicoUseCase adicionarItemServicoUseCase;
    private final AlterarItemServicoUseCase alterarItemServicoUseCase;
    private final IniciarExecucaoItemServicoUseCase iniciarExecucaoItemServicoUseCase;
    private final ConcluirExecucaoItemServicoUseCase concluirExecucaoItemServicoUseCase;

    public ItemOSServicoControllerClean(AdicionarItemServicoUseCase adicionarItemServicoUseCase,
                                        AlterarItemServicoUseCase alterarItemServicoUseCase,
                                        IniciarExecucaoItemServicoUseCase iniciarExecucaoItemServicoUseCase,
                                        ConcluirExecucaoItemServicoUseCase concluirExecucaoItemServicoUseCase) {
        this.adicionarItemServicoUseCase = adicionarItemServicoUseCase;
        this.alterarItemServicoUseCase = alterarItemServicoUseCase;
        this.iniciarExecucaoItemServicoUseCase = iniciarExecucaoItemServicoUseCase;
        this.concluirExecucaoItemServicoUseCase = concluirExecucaoItemServicoUseCase;
    }

    public void adicionarItemServico(AdicionarItemServicoCommand command) {
        adicionarItemServicoUseCase.executar(command);
    }

    public void alterarItemServico(AlterarItemServicoCommand command) {
        alterarItemServicoUseCase.executar(command);
    }

    public void iniciarExecucaoItemServico(IniciarExecucaoItemServicoCommand command) {
        iniciarExecucaoItemServicoUseCase.executar(command);
    }

    public void concluirExecucaoItemServico(ConcluirExecucaoItemServicoCommand command) {
        concluirExecucaoItemServicoUseCase.executar(command);
    }

}