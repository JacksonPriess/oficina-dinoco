package com.dinoco.oficina.ordemservico.infrastructure.config;

import com.dinoco.oficina.ordemservico.adapters.controllers.ItemOSServicoControllerClean;
import com.dinoco.oficina.ordemservico.application.gateways.CatalogoServicoGateway;
import com.dinoco.oficina.ordemservico.application.gateways.FuncionarioGateway;
import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemservico.AdicionarItemServicoHandler;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemservico.AdicionarItemServicoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemservico.AlterarItemServicoHandler;
import com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemservico.AlterarItemServicoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirexecucaoitemservico.ConcluirExecucaoItemServicoHandler;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirexecucaoitemservico.ConcluirExecucaoItemServicoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucaoitemservico.IniciarExecucaoItemServicoHandler;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucaoitemservico.IniciarExecucaoItemServicoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ItemOSServicoConfig {

    @Bean
    public AdicionarItemServicoUseCase adicionarItemServicoUseCase(OrdemServicoCommandGateway ordemServicoCommandGateway, CatalogoServicoGateway catalogoServicoGateway) {
        return new AdicionarItemServicoHandler(ordemServicoCommandGateway, catalogoServicoGateway);
    }

    @Bean
    public AlterarItemServicoUseCase alterarItemServicoUseCase(OrdemServicoCommandGateway ordemServicoCommandGateway, FuncionarioGateway funcionarioGateway) {
        return new AlterarItemServicoHandler(ordemServicoCommandGateway, funcionarioGateway);
    }

    @Bean
    public IniciarExecucaoItemServicoUseCase iniciarExecucaoItemServicoUseCase(OrdemServicoCommandGateway ordemServicoCommandGateway) {
        return new IniciarExecucaoItemServicoHandler(ordemServicoCommandGateway);
    }

    @Bean
    public ConcluirExecucaoItemServicoUseCase concluirExecucaoItemServicoUseCase(OrdemServicoCommandGateway ordemServicoCommandGateway) {
        return new ConcluirExecucaoItemServicoHandler(ordemServicoCommandGateway);
    }

    @Bean
    public ItemOSServicoControllerClean itemOSServicoControllerClean(AdicionarItemServicoUseCase adicionarItemServicoUseCase,
                                                                     AlterarItemServicoUseCase alterarItemServicoUseCase,
                                                                     IniciarExecucaoItemServicoUseCase iniciarExecucaoItemServicoUseCase,
                                                                     ConcluirExecucaoItemServicoUseCase concluirExecucaoItemServicoUseCase) {
        return new ItemOSServicoControllerClean(adicionarItemServicoUseCase,
                alterarItemServicoUseCase,
                iniciarExecucaoItemServicoUseCase,
                concluirExecucaoItemServicoUseCase
        );
    }
}