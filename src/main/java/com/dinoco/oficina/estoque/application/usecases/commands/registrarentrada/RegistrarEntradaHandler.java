package com.dinoco.oficina.estoque.application.usecases.commands.registrarentrada;

import com.dinoco.oficina.estoque.application.gateways.EstoqueCommandGateway;
import com.dinoco.oficina.estoque.domain.MovimentacaoEstoque;
import com.dinoco.oficina.estoque.domain.SaldoEstoque;
import com.dinoco.oficina.estoque.domain.TipoMovimentacao;

public class RegistrarEntradaHandler implements RegistrarEntradaUseCase {

    private final EstoqueCommandGateway estoqueGateway;

    public RegistrarEntradaHandler(EstoqueCommandGateway estoqueGateway) {
        this.estoqueGateway = estoqueGateway;
    }

    @Override
    public void executar(RegistrarEntradaCommand command) {

        SaldoEstoque saldo = estoqueGateway.buscarSaldoPorProdutoIdParaAlteracao(command.produtoId())
                .orElseThrow(() -> new IllegalArgumentException("Prateleira não encontrada para este produto."));

        // 2. Altera o Domínio Rico (Aplica a matemática e validações)
        saldo.adicionarEntrada(command.quantidade());

        // 3. Cria o histórico
        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque(
                command.produtoId(),
                TipoMovimentacao.ENTRADA,
                command.quantidade(),
                command.observacao()
        );

        // 4. Manda salvar os dois de forma atômica
        estoqueGateway.salvar(saldo, movimentacao);
    }
}
