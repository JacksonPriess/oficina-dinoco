package com.dinoco.oficina.ordemservico.application.usecases.commands.finalizarexecucao;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.application.saga.OrquestradorPagamento;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import com.dinoco.oficina.ordemservico.domain.enums.StatusOS;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class FinalizarExecucaoHandler implements FinalizarExecucaoUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;

    private final OrquestradorPagamento orquestradorPagamento;

    public FinalizarExecucaoHandler(OrdemServicoCommandGateway ordemServicoCommandGateway,
                                        OrquestradorPagamento orquestradorPagamento) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
        this.orquestradorPagamento = orquestradorPagamento;
    }

    @Override
    public void executar(FinalizarExecucaoCommand command) {

        OrdemServico ordemServico = ordemServicoCommandGateway.bloquearParaAlteracao(command.osId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada."));

        // OS finalizada anteriormente pode iniciar a cobrança ao repetir a operação.
        if (ordemServico.getStatus() == StatusOS.FINALIZADA) {
            orquestradorPagamento.iniciar(ordemServico.getId(), ordemServico.getValorTotalOS());
            return;
        }

        ordemServico.finalizarExecucao();

        ordemServicoCommandGateway.salvar(ordemServico);
        orquestradorPagamento.iniciar(ordemServico.getId(), ordemServico.getValorTotalOS());

        Duration duracao = Duration.between(ordemServico.getDataInicioExecucao(), ordemServico.getDataFinalExecucao());
        log.info("evento=os_execucao_concluida osId={} duracaoMs={}", ordemServico.getId(), duracao.toMillis());
    }
}