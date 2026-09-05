package com.dinoco.oficina.ordemservico.application.usecases.commands.decisaoclienteautenticado;

import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.application.usecases.commands.aprovar.AprovarOrcamentoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.aprovar.AprovarOrcamentoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.reprovar.ReprovarOrcamentoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.reprovar.ReprovarOrcamentoUseCase;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DecisaoClienteAutenticadoHandler implements DecisaoClienteAutenticadoUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;
    private final AprovarOrcamentoUseCase aprovarOrcamentoUseCase;
    private final ReprovarOrcamentoUseCase reprovarOrcamentoUseCase;

    public DecisaoClienteAutenticadoHandler(
            OrdemServicoCommandGateway ordemServicoCommandGateway,
            AprovarOrcamentoUseCase aprovarOrcamentoUseCase,
            ReprovarOrcamentoUseCase reprovarOrcamentoUseCase) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
        this.aprovarOrcamentoUseCase = aprovarOrcamentoUseCase;
        this.reprovarOrcamentoUseCase = reprovarOrcamentoUseCase;
    }

    @Override
    public void executar(DecisaoClienteAutenticadoCommand command) {
        log.info(
                "Processando decisão de orçamento do cliente. clienteId={}, codigoRastreio={}",
                command.clienteId(),
                command.codigoRastreio()
        );

        OrdemServico os = ordemServicoCommandGateway
                .buscarPorCodigoRastreioEClienteParaAlteracao(
                        command.codigoRastreio(),
                        command.clienteId()
                )
                .orElseThrow(() -> {
                    log.warn(
                            "Ordem de serviço não encontrada ou não pertence ao cliente. clienteId={}, codigoRastreio={}",
                            command.clienteId(),
                            command.codigoRastreio()
                    );

                    return new RecursoNaoEncontradoException("Ordem de serviço não encontrada");
                });


        if ("APROVADO".equalsIgnoreCase(command.statusDecisao())) {
            log.info(
                    "Cliente aprovou orçamento. clienteId={}, osId={}, codigoRastreio={}",
                    command.clienteId(),
                    os.getId(),
                    command.codigoRastreio()
            );
            aprovarOrcamentoUseCase.executar(new AprovarOrcamentoCommand(os.getId()));

        } else if ("RECUSADO".equalsIgnoreCase(command.statusDecisao())) {
            log.info(
                    "Cliente recusou orçamento. clienteId={}, osId={}, codigoRastreio={}",
                    command.clienteId(),
                    os.getId(),
                    command.codigoRastreio()
            );
            reprovarOrcamentoUseCase.executar(new ReprovarOrcamentoCommand(os.getId()));
        } else {
            log.warn(
                    "Decisão inválida recebida. clienteId={}, osId={}, statusDecisao={}",
                    command.clienteId(),
                    os.getId(),
                    command.statusDecisao()
            );
            
            throw new IllegalArgumentException(
                    "Status de decisão inválido. Use APROVADO ou RECUSADO."
            );
        }
    }
}