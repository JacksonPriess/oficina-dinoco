package com.dinoco.oficina.estoque.application.listeners;

import com.dinoco.oficina.estoque.application.gateways.EstoqueCommandGateway;
import com.dinoco.oficina.estoque.domain.MovimentacaoEstoque;
import com.dinoco.oficina.estoque.domain.SaldoEstoque;
import com.dinoco.oficina.estoque.domain.TipoMovimentacao;
import com.dinoco.oficina.shared.events.ProdutoCadastradoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
@Slf4j
public class ProdutoCadastradoListener {

    private final EstoqueCommandGateway estoqueGateway;

    public ProdutoCadastradoListener(EstoqueCommandGateway estoqueGateway) {
        this.estoqueGateway = estoqueGateway;
    }

    /**
     * Este método é acionado automaticamente pelo Spring quando qualquer lugar
     * da aplicação chamar o ApplicationEventPublisher.publishEvent().
     *
     * Esse Listener, será executado apenas no Cadastro de um Produto novo.
     * Vai gerar um registro para a tabela de saldo de estoque para manter a integridade das quantidades.
     */
    @EventListener
    public void criarPrateleiraAoCadastrarProduto(ProdutoCadastradoEvent event) {
        log.info("Evento ProdutoCadastrado recebido: produtoId={}, quantidade={}", event.produtoId(), event.quantidade());
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
            log.debug("Criada movimentacao inicial: produtoId={}, tipo={}, quantidade={}",
                    event.produtoId(), TipoMovimentacao.ENTRADA, event.quantidade());
        } else {
            log.debug("Nenhuma quantidade inicial informada ou valor zero para produtoId={}", event.produtoId());
        }

        try {
            estoqueGateway.salvar(saldo, movimentacao);
            log.info("Saldo e movimentacao salvos com sucesso para produtoId={}", event.produtoId());
        } catch (Exception e) {
            log.error("Erro ao salvar saldo/movimentacao para produtoId={}: {}", event.produtoId(), e.getMessage(), e);
            throw e;
        }
    }
}