package com.dinoco.oficina.estoque.application.listeners;

import com.dinoco.oficina.estoque.application.gateways.EstoqueCommandGateway;
import com.dinoco.oficina.estoque.domain.MovimentacaoEstoque;
import com.dinoco.oficina.estoque.domain.TipoMovimentacao;
import com.dinoco.oficina.ordemservico.domain.models.ItemOSProduto;
import com.dinoco.oficina.shared.events.ExecucaoIniciadaEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ExecucaoIniciadaListener {

    private final EstoqueCommandGateway estoqueGateway;

    public ExecucaoIniciadaListener(EstoqueCommandGateway estoqueGateway) {
        this.estoqueGateway = estoqueGateway;
    }

    /**
     * Este método é acionado automaticamente pelo Spring quando qualquer lugar
     * da aplicação chamar o ApplicationEventPublisher.publishEvent().
     */
    @EventListener
    public void consumirItensDoEstoque(ExecucaoIniciadaEvent event) {

        for (ItemOSProduto itemOSProduto : event.itensOSProduto()) {

            var produtoSaldoEstoque = estoqueGateway.buscarSaldoPorProdutoIdParaAlteracao(itemOSProduto.getProdutoId())
                    .orElseThrow(() -> new IllegalArgumentException("Prateleira não encontrada para este produto."));

            produtoSaldoEstoque.consumirQuantidadeReservadaEFisica(itemOSProduto.getQuantidade());

            var movimentacao = new MovimentacaoEstoque(
                    itemOSProduto.getProdutoId(),
                    TipoMovimentacao.BAIXA_EXECUCAO_OS,
                    itemOSProduto.getQuantidade(),
                    "Baixa por início de execução: " + event.osId()
            );

            estoqueGateway.salvar(produtoSaldoEstoque, movimentacao);
        }
    }
}

