package com.dinoco.oficina.estoque.application.usecases.commands.ajustarinventario;

import com.dinoco.oficina.estoque.application.gateways.EstoqueCommandGateway;
import com.dinoco.oficina.estoque.domain.MovimentacaoEstoque;
import com.dinoco.oficina.estoque.domain.SaldoEstoque;
import com.dinoco.oficina.estoque.domain.TipoMovimentacao;
import java.math.BigDecimal;

public class AjustarInventarioHandler implements AjustarInventarioUseCase {

    private final EstoqueCommandGateway estoqueGateway;

    public AjustarInventarioHandler(EstoqueCommandGateway estoqueGateway) {
        this.estoqueGateway = estoqueGateway;
    }

    @Override
    public void executar(AjustarInventarioCommand command) {

        SaldoEstoque saldo = estoqueGateway.buscarSaldoPorProdutoIdParaAlteracao(command.produtoId())
                .orElseThrow(() -> new IllegalArgumentException("Prateleira não encontrada para este produto."));

        if (!command.versao().equals(saldo.getVersao())) {
            throw new IllegalStateException("O estoque foi alterado por outra operação. Por favor, recarregue os dados e tente novamente.");
        }

        BigDecimal quantidadeAtual = saldo.getQuantidadeReal();
        BigDecimal diferenca = command.quantidadeContadaNaPrateleira().subtract(quantidadeAtual);

        if (diferenca.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("A quantidade contada é igual ao saldo atual. Nenhum ajuste necessário.");
        }

        TipoMovimentacao tipo;
        BigDecimal quantidadeAbsoluta = diferenca.abs();

        if (diferenca.compareTo(BigDecimal.ZERO) > 0) {
            tipo = TipoMovimentacao.AJUSTE_ENTRADA;
            saldo.adicionarEntrada(quantidadeAbsoluta);
        } else {
            tipo = TipoMovimentacao.AJUSTE_SAIDA;
            saldo.retirar(quantidadeAbsoluta);
        }

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque(
                command.produtoId(),
                tipo,
                quantidadeAbsoluta,
                command.observacao() != null ? command.observacao() : "Ajuste manual de inventário"
        );

        estoqueGateway.salvar(saldo, movimentacao);
    }
}
