package com.dinoco.oficina.estoque.application.listeners;

import com.dinoco.oficina.estoque.application.gateways.EstoqueCommandGateway;
import com.dinoco.oficina.estoque.domain.MovimentacaoEstoque;
import com.dinoco.oficina.estoque.domain.SaldoEstoque;
import com.dinoco.oficina.estoque.domain.TipoMovimentacao;
import com.dinoco.oficina.ordemservico.domain.models.ItemOSProduto;
import com.dinoco.oficina.shared.events.OrcamentoAprovadoEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
public class OrcamentoAprovadoListener {

    private final EstoqueCommandGateway estoqueGateway;

    public OrcamentoAprovadoListener(EstoqueCommandGateway estoqueGateway) {
        this.estoqueGateway = estoqueGateway;
    }

    /**
     * Este método é acionado automaticamente pelo Spring quando qualquer lugar
     * da aplicação chamar o ApplicationEventPublisher.publishEvent().
     */
    @EventListener
    public void reservarItensNoEstoque(OrcamentoAprovadoEvent event) {
        for (ItemOSProduto itemOSProduto : event.itensOSProduto()) {

            SaldoEstoque saldo = estoqueGateway.buscarSaldoPorProdutoIdParaAlteracao(itemOSProduto.getProdutoId())
                    .orElseThrow(() -> new IllegalArgumentException("Prateleira não encontrada para este produto."));

            MovimentacaoEstoque movimentacao = null;

            //Quando o orcamento for aprovado, o sistema deve reservar a quantidade dos itens no estoque,
            // para garantir que eles não sejam vendidos para outro cliente.
            saldo.adicionarQuantidadeReservada(itemOSProduto.getQuantidade());

            movimentacao = new MovimentacaoEstoque(
                    itemOSProduto.getProdutoId(),
                    TipoMovimentacao.RESERVA_OS,
                    itemOSProduto.getQuantidade(),
                    "Reserva OS: " + event.osId()
            );

            estoqueGateway.salvar(saldo, movimentacao);
        }

    }
}