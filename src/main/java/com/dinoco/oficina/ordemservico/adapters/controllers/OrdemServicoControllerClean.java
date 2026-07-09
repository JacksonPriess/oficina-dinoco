package com.dinoco.oficina.ordemservico.adapters.controllers;

import com.dinoco.oficina.ordemservico.application.usecases.commands.abrir.AbrirOrdemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.abrir.AbrirOrdemServicoOutput;
import com.dinoco.oficina.ordemservico.application.usecases.commands.abrir.AbrirOrdemServicoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.aprovar.AprovarOrcamentoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.aprovar.AprovarOrcamentoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluir.ConcluirOrdemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluir.ConcluirOrdemServicoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirdiagnostico.ConcluirDiagnosticoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirdiagnostico.ConcluirDiagnosticoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.enviarorcamento.EnviarOrcamentoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.enviarorcamento.EnviarOrcamentoOutput;
import com.dinoco.oficina.ordemservico.application.usecases.commands.enviarorcamento.EnviarOrcamentoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.finalizarexecucao.FinalizarExecucaoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.finalizarexecucao.FinalizarExecucaoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciardiagnostico.IniciarDiagnosticoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucao.IniciarExecucaoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucao.IniciarExecucaoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.reprovar.ReprovarOrcamentoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.reprovar.ReprovarOrcamentoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciardiagnostico.IniciarDiagnosticoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.verificarestoque.VerificarEstoqueCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.verificarestoque.VerificarEstoqueOutput;
import com.dinoco.oficina.ordemservico.application.usecases.commands.verificarestoque.VerificarEstoqueUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarporid.BuscarOSPorIdOuput;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarporid.BuscarOSPorIdQuery;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarporid.BuscarOSPorIdUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarpornumero.BuscarOSPorCodigoRastreioOuput;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarpornumero.BuscarOSPorCodigoRastreioQuery;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarpornumero.BuscarOSPorCodigoRastreioUseCase;

public class OrdemServicoControllerClean {

    private final AbrirOrdemServicoUseCase abrirOrdemServicoUseCase;
    private final IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase;
    private final ConcluirDiagnosticoUseCase concluirDiagnosticoUseCase;
    private final EnviarOrcamentoUseCase enviarOrcamentoUseCase;
    private final ReprovarOrcamentoUseCase reprovarOrcamentoUseCase;
    private final AprovarOrcamentoUseCase aprovarOrcamentoUseCase;
    private final VerificarEstoqueUseCase verificarEstoqueUseCase;
    private final IniciarExecucaoUseCase iniciarExecucaoUseCase;
    private final FinalizarExecucaoUseCase finalizarExecucaoUseCase;
    private final ConcluirOrdemServicoUseCase concluirOrdemServicoUseCase;
    private final BuscarOSPorIdUseCase buscarOSPorIdUseCase;
    private final BuscarOSPorCodigoRastreioUseCase buscarOSPorCodigoRastreioUseCase;



    public OrdemServicoControllerClean(AbrirOrdemServicoUseCase abrirOrdemServicoUseCase,
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
        this.abrirOrdemServicoUseCase = abrirOrdemServicoUseCase;
        this.iniciarDiagnosticoUseCase = iniciarDiagnosticoUseCase;
        this.concluirDiagnosticoUseCase = concluirDiagnosticoUseCase;
        this.enviarOrcamentoUseCase = enviarOrcamentoUseCase;
        this.reprovarOrcamentoUseCase = reprovarOrcamentoUseCase;
        this.aprovarOrcamentoUseCase = aprovarOrcamentoUseCase;
        this.verificarEstoqueUseCase = verificarEstoqueUseCase;
        this.iniciarExecucaoUseCase = iniciarExecucaoUseCase;
        this.finalizarExecucaoUseCase = finalizarExecucaoUseCase;
        this.concluirOrdemServicoUseCase = concluirOrdemServicoUseCase;
        this.buscarOSPorIdUseCase = buscarOSPorIdUseCase;
        this.buscarOSPorCodigoRastreioUseCase = buscarOSPorCodigoRastreioUseCase;
    }

    public AbrirOrdemServicoOutput abrirOrdemServico(AbrirOrdemServicoCommand command) {
        return abrirOrdemServicoUseCase.executar(command);
    }

    public void iniciarDiagnostico(IniciarDiagnosticoCommand command){
        iniciarDiagnosticoUseCase.executar(command);
    }

    public void concluirDiagnostico(ConcluirDiagnosticoCommand command) {
        concluirDiagnosticoUseCase.executar(command);
    }

    public EnviarOrcamentoOutput enviarOrcamento(EnviarOrcamentoCommand comnand) {
        return enviarOrcamentoUseCase.executar(comnand);
    }

    public void reprovarOrcamento(ReprovarOrcamentoCommand command) {
        reprovarOrcamentoUseCase.executar(command);
    }

    public void aprovarOrcamento(AprovarOrcamentoCommand command) {
        aprovarOrcamentoUseCase.executar(command);
    }

    public VerificarEstoqueOutput verificarEstoque(VerificarEstoqueCommand command) {
        return verificarEstoqueUseCase.executar(command);
    }

    public void iniciarExecucaoOS(IniciarExecucaoCommand command) {
        iniciarExecucaoUseCase.executar(command);
    }

    public void finalizarExecucaoOS(FinalizarExecucaoCommand command) {
        finalizarExecucaoUseCase.executar(command);
    }

    public void concluirOS(ConcluirOrdemServicoCommand command ){
        concluirOrdemServicoUseCase.executar(command);
    }

    public BuscarOSPorIdOuput buscarOSPorId(BuscarOSPorIdQuery query) {
        return buscarOSPorIdUseCase.executar(query);
    }

    public BuscarOSPorCodigoRastreioOuput buscarOSPorCodigoRastreio(BuscarOSPorCodigoRastreioQuery query) {
        return buscarOSPorCodigoRastreioUseCase.executar(query);
    }
}