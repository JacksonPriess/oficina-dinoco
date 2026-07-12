package com.dinoco.oficina.ordemservico.application.usecases.commands.decisaoclienteorcamento;

import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.application.usecases.commands.aprovar.AprovarOrcamentoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.aprovar.AprovarOrcamentoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.reprovar.ReprovarOrcamentoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.reprovar.ReprovarOrcamentoUseCase;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DecisaoClienteHandler implements DecisaoClienteUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;
    private final AprovarOrcamentoUseCase aprovarOrcamentoUseCase;
    private final ReprovarOrcamentoUseCase reprovarOrcamentoUseCase;

    public DecisaoClienteHandler(
            OrdemServicoCommandGateway ordemServicoCommandGateway,
            AprovarOrcamentoUseCase aprovarOrcamentoUseCase,
            ReprovarOrcamentoUseCase reprovarOrcamentoUseCase) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
        this.aprovarOrcamentoUseCase = aprovarOrcamentoUseCase;
        this.reprovarOrcamentoUseCase = reprovarOrcamentoUseCase;
    }

    @Override
    public void executar(DecisaoClienteCommand command) {
        log.info("Webhook: Processando decisão externa para a OS Rastreio: {}", command.codigoRastreio());

        OrdemServico os = ordemServicoCommandGateway.buscarPorCodigoRastreioParaAlteracao(command.codigoRastreio())
                .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada para o código de rastreio: " + command.codigoRastreio()));

        if ("APROVADO".equalsIgnoreCase(command.statusDecisao())) {
            log.info("Decisão: APROVADO. Delegando para o fluxo padrão de aprovação.");
            aprovarOrcamentoUseCase.executar(new AprovarOrcamentoCommand(os.getId()));

        } else if ("RECUSADO".equalsIgnoreCase(command.statusDecisao())) {
            log.info("Decisão: RECUSADO. Delegando para o fluxo padrão de reprovação. Motivo: {}", command.observacao());
            reprovarOrcamentoUseCase.executar(new ReprovarOrcamentoCommand(os.getId()));
        } else {
            throw new IllegalArgumentException("Status de decisão inválido no webhook. Use APROVADO ou RECUSADO.");
        }
    }
}