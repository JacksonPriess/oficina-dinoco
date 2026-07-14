package com.dinoco.oficina.ordemservico.application.usecases.commands.aprovar;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoEventPublisher;
import com.dinoco.oficina.ordemservico.application.gateways.VerificadorEstoqueGateway;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import com.dinoco.oficina.shared.events.OrcamentoAprovadoEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AprovarOrcamentoHandler implements AprovarOrcamentoUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;
    private final OrdemServicoEventPublisher ordemServicoEventPublisher;
    private final VerificadorEstoqueGateway verificadorEstoqueGateway;

    public AprovarOrcamentoHandler(
            OrdemServicoCommandGateway ordemServicoCommandGateway,
            OrdemServicoEventPublisher ordemServicoEventPublisher,
            VerificadorEstoqueGateway verificadorEstoqueGateway) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
        this.ordemServicoEventPublisher = ordemServicoEventPublisher;
        this.verificadorEstoqueGateway = verificadorEstoqueGateway;
    }

    @Override
    public void executar(AprovarOrcamentoCommand command) {

        OrdemServico os = ordemServicoCommandGateway.buscarParaAlteracao(command.osId())
                .orElseThrow(() -> new IllegalArgumentException("OS não encontrada."));

        boolean temEstoqueParaTudo = verificadorEstoqueGateway.todasAsPecasEstaoDisponiveis(os.getItensProduto());
        if ( temEstoqueParaTudo ) {
            log.info("Tem estoque para todos os produtos da OS. Marcando como AGUARDANDO_EXECUCAO");
            os.marcarProntaParaExecucao(); // Vai para AGUARDANDO_EXECUCAO
        } else {
            log.warn("Não tem estoque para todos os produtos da OS. Marcando como AGUARDANDO_FORNECEDOR");
            os.marcarAguardandoFornecedor(); // Vai para AGUARDANDO_FORNECEDOR
        }

        ordemServicoCommandGateway.salvar(os);

        // O módulo de Estoque vai escutar isso, iterar sobre os produtos e tentar reservar!
        ordemServicoEventPublisher.publicarOrcamentoAprovado(new OrcamentoAprovadoEvent(os.getId(), os.getItensProduto()));
    }
}
