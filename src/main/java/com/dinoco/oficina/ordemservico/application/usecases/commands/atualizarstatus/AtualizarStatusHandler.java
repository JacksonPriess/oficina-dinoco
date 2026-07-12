package com.dinoco.oficina.ordemservico.application.usecases.commands.atualizarstatus;

import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluir.ConcluirOrdemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluir.ConcluirOrdemServicoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirdiagnostico.ConcluirDiagnosticoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirdiagnostico.ConcluirDiagnosticoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.enviarorcamento.EnviarOrcamentoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.enviarorcamento.EnviarOrcamentoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.finalizarexecucao.FinalizarExecucaoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.finalizarexecucao.FinalizarExecucaoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciardiagnostico.IniciarDiagnosticoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciardiagnostico.IniciarDiagnosticoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucao.IniciarExecucaoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucao.IniciarExecucaoUseCase;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AtualizarStatusHandler implements AtualizarStatusUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;

    private final IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase;
    private final ConcluirDiagnosticoUseCase concluirDiagnosticoUseCase;
    private final EnviarOrcamentoUseCase enviarOrcamentoUseCase;
    private final IniciarExecucaoUseCase iniciarExecucaoUseCase;
    private final FinalizarExecucaoUseCase finalizarExecucaoUseCase;
    private final ConcluirOrdemServicoUseCase concluirOrdemServicoUseCase;

    public AtualizarStatusHandler(
            OrdemServicoCommandGateway ordemServicoCommandGateway,
            IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase,
            ConcluirDiagnosticoUseCase concluirDiagnosticoUseCase,
            EnviarOrcamentoUseCase enviarOrcamentoUseCase,
            IniciarExecucaoUseCase iniciarExecucaoUseCase,
            FinalizarExecucaoUseCase finalizarExecucaoUseCase,
            ConcluirOrdemServicoUseCase concluirOrdemServicoUseCase) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
        this.iniciarDiagnosticoUseCase = iniciarDiagnosticoUseCase;
        this.concluirDiagnosticoUseCase = concluirDiagnosticoUseCase;
        this.enviarOrcamentoUseCase = enviarOrcamentoUseCase;
        this.iniciarExecucaoUseCase = iniciarExecucaoUseCase;
        this.finalizarExecucaoUseCase = finalizarExecucaoUseCase;
        this.concluirOrdemServicoUseCase = concluirOrdemServicoUseCase;
    }

    @Override
    public void executar(AtualizarStatusCommand command) {
        log.info("Processando webhook de status. OS Rastreio: {}, Ação: {}", command.codigoRastreio(), command.acao());

        OrdemServico ordemServico = ordemServicoCommandGateway.buscarPorCodigoRastreioParaAlteracao(command.codigoRastreio())
                .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada para o código de rastreio: " + command.codigoRastreio()));

        Long osId = ordemServico.getId();

        switch (command.acao().toUpperCase()) {
            case "INICIAR_DIAGNOSTICO" -> iniciarDiagnosticoUseCase.executar(new IniciarDiagnosticoCommand(osId));
            case "CONCLUIR_DIAGNOSTICO" -> concluirDiagnosticoUseCase.executar(new ConcluirDiagnosticoCommand(osId, command.laudo()));
            case "ENVIAR_ORCAMENTO" -> enviarOrcamentoUseCase.executar(new EnviarOrcamentoCommand(osId));
            case "INICIAR_EXECUCAO" -> iniciarExecucaoUseCase.executar(new IniciarExecucaoCommand(osId));
            case "FINALIZAR_EXECUCAO" -> finalizarExecucaoUseCase.executar(new FinalizarExecucaoCommand(osId));
            case "CONCLUIR" -> concluirOrdemServicoUseCase.executar(new ConcluirOrdemServicoCommand(osId));
            default -> throw new IllegalArgumentException("Ação de webhook não suportada: " + command.acao() +
                    ", Use INICIAR_DIAGNOSTICO, CONCLUIR_DIAGNOSTICO, ENVIAR_ORCAMENTO, INICIAR_EXECUCAO, FINALIZAR_EXECUCAO ou CONCLUIR.");
        }
    }
}