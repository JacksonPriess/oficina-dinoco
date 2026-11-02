package com.dinoco.oficina.estoque.application.usecases.commands.inicializar;

import com.dinoco.oficina.estoque.application.gateways.EstoqueCommandGateway;
import com.dinoco.oficina.estoque.application.gateways.InicializacaoEstoqueGateway;
import com.dinoco.oficina.estoque.domain.*;
import com.dinoco.oficina.shared.events.ProdutoCadastradoEvent;

public class InicializarEstoqueHandler implements InicializarEstoqueUseCase {

    private final EstoqueCommandGateway estoque;
    private final InicializacaoEstoqueGateway inicializacao;

    public InicializarEstoqueHandler(EstoqueCommandGateway estoque, InicializacaoEstoqueGateway inicializacao) {
        this.estoque = estoque;
        this.inicializacao = inicializacao;
    }

    @Override
    public void executar(ProdutoCadastradoEvent evento) {
        if (!inicializacao.registrarSeNovo(evento))
            return;

        if (estoque.buscarSaldoPorProdutoIdParaAlteracao(evento.produtoId()).isPresent())
            throw new IllegalStateException("Produto já possui saldo; reconciliar a inicialização antes de reprocessar.");

        var saldo = new SaldoEstoque(evento.produtoId());

        MovimentacaoEstoque movimentacao = null;
        if (evento.quantidade() != null && evento.quantidade().signum() > 0) {
            saldo.adicionarEntrada(evento.quantidade());
            movimentacao = new MovimentacaoEstoque(evento.produtoId(), TipoMovimentacao.ENTRADA,
                    evento.quantidade(), "Saldo inicial de cadastro de produto");
        }

        estoque.salvar(saldo, movimentacao);
    }
}