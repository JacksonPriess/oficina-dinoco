package com.dinoco.oficina.ordemservico.application.usecases.commands.concluirdiagnostico;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class ConcluirDiagnosticoHandler implements ConcluirDiagnosticoUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;

    public ConcluirDiagnosticoHandler(OrdemServicoCommandGateway ordemServicoCommandGateway) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
    }

    @Override
    public void executar(ConcluirDiagnosticoCommand command) {
        OrdemServico ordemServico = ordemServicoCommandGateway.buscarParaAlteracao(command.osId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada."));

        ordemServico.concluirDiagnostico(command.laudo());

        ordemServicoCommandGateway.salvar(ordemServico);

        Duration duracao = Duration.between(ordemServico.getDataInicioDiagnostico(), ordemServico.getDataFinalDiagnostico());
        log.info("evento=os_diagnostico_concluido osId={} duracaoMs={}", ordemServico.getId(), duracao.toMillis());
    }
}