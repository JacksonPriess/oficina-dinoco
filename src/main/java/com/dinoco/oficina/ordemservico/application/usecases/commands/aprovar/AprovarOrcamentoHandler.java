package com.dinoco.oficina.ordemservico.application.usecases.commands.aprovar;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoEventPublisher;
import com.dinoco.oficina.ordemservico.application.gateways.VerificadorEstoqueGateway;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import com.dinoco.oficina.shared.events.OrcamentoAprovadoEvent;

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

        //Antes de reservar.
        boolean temEstoqueParaTudo = verificadorEstoqueGateway.todasAsPecasEstaoDisponiveis(os.getItensProduto());

        // 2. Altera o status (se estiver AGUARDANDO_APROVACAO, senão o Domínio lança erro!)
        // Nota: Neste momento vamos assumir que vai para AGUARDANDO_EXECUCAO ou AGUARDANDO_FORNECEDOR
        if ( temEstoqueParaTudo ) {
            os.marcarProntaParaExecucao(); // Vai para AGUARDANDO_EXECUCAO
        } else {
            os.marcarAguardandoFornecedor(); // Vai para AGUARDANDO_FORNECEDOR
        }

        // 3. Salva a OS
        ordemServicoCommandGateway.salvar(os);

        // 4. Mágica do Desacoplamento: Grita para o sistema que a OS foi aprovada.
        // O módulo de Estoque vai escutar isso, iterar sobre os produtos e tentar reservar!
        ordemServicoEventPublisher.publicarOrcamentoAprovado(new OrcamentoAprovadoEvent(os.getId(), os.getItensProduto()));
    }
}
