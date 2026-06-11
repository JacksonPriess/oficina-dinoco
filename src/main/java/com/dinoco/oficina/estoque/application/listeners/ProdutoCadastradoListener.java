package com.dinoco.oficina.estoque.application.listeners;

import com.dinoco.oficina.estoque.application.gateways.EstoqueCommandGateway;
import com.dinoco.oficina.estoque.domain.MovimentacaoEstoque;
import com.dinoco.oficina.estoque.domain.SaldoEstoque;
import com.dinoco.oficina.estoque.domain.TipoMovimentacao;
import com.dinoco.oficina.shared.events.ProdutoCadastradoEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class ProdutoCadastradoListener {

    private final EstoqueCommandGateway estoqueGateway;

    public ProdutoCadastradoListener(EstoqueCommandGateway estoqueGateway) {
        this.estoqueGateway = estoqueGateway;
    }

    /**
     * Este método é acionado automaticamente pelo Spring quando qualquer lugar
     * da aplicação chamar o ApplicationEventPublisher.publishEvent().
     */
    @EventListener
    public void criarPrateleiraAoCadastrarProduto(ProdutoCadastradoEvent event) {

        SaldoEstoque saldo = new SaldoEstoque(event.produtoId());
        MovimentacaoEstoque movimentacao = null;

        if (event.quantidade() != null && event.quantidade().compareTo(BigDecimal.ZERO) > 0) {

            saldo.adicionarEntrada(event.quantidade());

            movimentacao = new MovimentacaoEstoque(
                    event.produtoId(),
                    TipoMovimentacao.ENTRADA,
                    event.quantidade(),
                    "Saldo inicial de cadastro de produto"
            );
        }

        estoqueGateway.salvar(saldo, movimentacao);
    }
}