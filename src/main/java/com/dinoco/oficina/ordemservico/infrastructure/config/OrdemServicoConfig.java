package com.dinoco.oficina.ordemservico.infrastructure.config;


import com.dinoco.oficina.ordemservico.adapters.controllers.OrdemServicoControllerClean;
import com.dinoco.oficina.ordemservico.application.gateways.*;
import com.dinoco.oficina.ordemservico.application.usecases.commands.abrir.AbrirOrdemServicoHandler;
import com.dinoco.oficina.ordemservico.application.usecases.commands.abrir.AbrirOrdemServicoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.aprovar.AprovarOrcamentoHandler;
import com.dinoco.oficina.ordemservico.application.usecases.commands.aprovar.AprovarOrcamentoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluir.ConcluirOrdemServicoHandler;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluir.ConcluirOrdemServicoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirdiagnostico.ConcluirDiagnosticoHandler;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirdiagnostico.ConcluirDiagnosticoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.enviarorcamento.EnviarOrcamentoHandler;
import com.dinoco.oficina.ordemservico.application.usecases.commands.enviarorcamento.EnviarOrcamentoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.finalizarexecucao.FinalizarExecucaoHandler;
import com.dinoco.oficina.ordemservico.application.usecases.commands.finalizarexecucao.FinalizarExecucaoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciardiagnostico.IniciarDiagnosticoHandler;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciardiagnostico.IniciarDiagnosticoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucao.IniciarExecucaoHandler;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucao.IniciarExecucaoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.reprovar.ReprovarOrcamentoHandler;
import com.dinoco.oficina.ordemservico.application.usecases.commands.reprovar.ReprovarOrcamentoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.verificarestoque.VerificarEstoqueHandler;
import com.dinoco.oficina.ordemservico.application.usecases.commands.verificarestoque.VerificarEstoqueUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarporid.BuscarOSPorIdHandler;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarporid.BuscarOSPorIdUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarpornumero.BuscarOSPorCodigoRastreioHandler;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarpornumero.BuscarOSPorCodigoRastreioUseCase;
import com.dinoco.oficina.cliente.application.gateways.ClienteQueryGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrdemServicoConfig {

    @Bean
    public AbrirOrdemServicoUseCase abrirOrdemServicoUseCase(OrdemServicoCommandGateway ordemServicoCommandGateway,
                                                             ClienteQueryGateway clienteQueryGateway,
                                                             CatalogoProdutoGateway catalogoProdutoGateway,
                                                             CatalogoServicoGateway catalogoServicoGateway) {
        return new AbrirOrdemServicoHandler(ordemServicoCommandGateway, clienteQueryGateway, catalogoProdutoGateway, catalogoServicoGateway);
    }

    @Bean
    public IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase(OrdemServicoCommandGateway ordemServicoCommandGateway) {
        return new IniciarDiagnosticoHandler(ordemServicoCommandGateway);
    }

    @Bean
    public ConcluirDiagnosticoUseCase concluirDiagnosticoUseCase(OrdemServicoCommandGateway ordemServicoCommandGateway) {
        return new ConcluirDiagnosticoHandler(ordemServicoCommandGateway);
    }

    @Bean
    public EnviarOrcamentoUseCase enviarOrcamentoUseCase(OrdemServicoCommandGateway ordemServicoCommandGateway, ClienteContatoGateway clienteContatoGateway) {
        return new EnviarOrcamentoHandler(ordemServicoCommandGateway, clienteContatoGateway);
    }

    @Bean
    public ReprovarOrcamentoUseCase reprovarOrcamentoUseCase(OrdemServicoCommandGateway ordemServicoCommandGateway) {
        return new ReprovarOrcamentoHandler(ordemServicoCommandGateway);
    }

    @Bean
    public AprovarOrcamentoUseCase aprovarOrcamentoUseCase(OrdemServicoCommandGateway ordemServicoCommandGateway, OrdemServicoEventPublisher ordemServicoEventPublisher, VerificadorEstoqueGateway verificadorEstoqueGateway) {
        return new AprovarOrcamentoHandler(ordemServicoCommandGateway, ordemServicoEventPublisher, verificadorEstoqueGateway);
    }

    @Bean
    public VerificarEstoqueUseCase verificarEstoqueUseCase(OrdemServicoCommandGateway ordemServicoCommandGateway, VerificadorEstoqueGateway verificadorEstoqueGateway) {
        return new VerificarEstoqueHandler(verificadorEstoqueGateway, ordemServicoCommandGateway);
    }

    @Bean
    public IniciarExecucaoUseCase iniciarExecucaoUseCase(OrdemServicoCommandGateway ordemServicoCommandGateway, OrdemServicoEventPublisher ordemServicoEventPublisher) {
        return new IniciarExecucaoHandler(ordemServicoCommandGateway, ordemServicoEventPublisher);
    }

    @Bean
    public FinalizarExecucaoUseCase finalizarExecucaoUseCase(OrdemServicoCommandGateway ordemServicoCommandGateway) {
        return new FinalizarExecucaoHandler(ordemServicoCommandGateway);
    }

    @Bean
    public ConcluirOrdemServicoUseCase concluirOrdemServicoUseCase(OrdemServicoCommandGateway ordemServicoCommandGateway) {
        return new ConcluirOrdemServicoHandler(ordemServicoCommandGateway);
    }

    @Bean
    public BuscarOSPorIdUseCase buscarOSPorIdUseCase(OrdemServicoQueryGateway ordemServicoQueryGateway) {
        return new BuscarOSPorIdHandler(ordemServicoQueryGateway);
    }

    @Bean
    public BuscarOSPorCodigoRastreioUseCase buscarOSPorCodigoRastreioUseCase(OrdemServicoQueryGateway ordemServicoQueryGateway) {
        return new BuscarOSPorCodigoRastreioHandler(ordemServicoQueryGateway);
    }

    @Bean
    public OrdemServicoControllerClean ordemServicoControllerClean(AbrirOrdemServicoUseCase abrirOrdemServicoUseCase,
                                                                   IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase,
                                                                   ConcluirDiagnosticoUseCase concluirDiagnosticoUseCase,
                                                                   EnviarOrcamentoUseCase enviarOrcamentoUseCase,
                                                                   ReprovarOrcamentoUseCase reprovarOrcamentoUseCase,
                                                                   AprovarOrcamentoUseCase aprovarOrcamentoUseCase,
                                                                   VerificarEstoqueUseCase verificarEstoqueUseCase,
                                                                   IniciarExecucaoUseCase iniciarExecucaoUseCase,
                                                                   FinalizarExecucaoUseCase finalizarExecucaoUseCase,
                                                                   ConcluirOrdemServicoUseCase concluirOrdemServicoUseCase,
                                                                   BuscarOSPorIdUseCase buscarOSPorIdUseCase,
                                                                   BuscarOSPorCodigoRastreioUseCase buscarOSPorCodigoRastreioUseCase) {
        return new OrdemServicoControllerClean(abrirOrdemServicoUseCase,
                iniciarDiagnosticoUseCase,
                concluirDiagnosticoUseCase,
                enviarOrcamentoUseCase,
                reprovarOrcamentoUseCase,
                aprovarOrcamentoUseCase,
                verificarEstoqueUseCase,
                iniciarExecucaoUseCase,
                finalizarExecucaoUseCase,
                concluirOrdemServicoUseCase,
                buscarOSPorIdUseCase,
                buscarOSPorCodigoRastreioUseCase);
    }
}