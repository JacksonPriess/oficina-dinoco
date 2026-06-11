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

        if (command.diferenca().compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("A diferença de ajuste não pode ser zero.");
        }

        // 2. Busca a prateleira do produto
        SaldoEstoque saldo = estoqueGateway.buscarSaldoPorProdutoIdParaAlteracao(command.produtoId())
                .orElseThrow(() -> new IllegalArgumentException("Prateleira não encontrada para este produto."));

        // 3. Define o tipo de movimento e aplica a regra no Domínio Rico
        TipoMovimentacao tipo;
        BigDecimal quantidadeAbsoluta = command.diferenca().abs();
        // Trabalhamos com o valor positivo para o extrato

        if (command.diferenca().compareTo(BigDecimal.ZERO) > 0) {
            tipo = TipoMovimentacao.AJUSTE_ENTRADA;
            saldo.adicionarEntrada(quantidadeAbsoluta);
        } else {
            tipo = TipoMovimentacao.AJUSTE_SAIDA;
            saldo.retirar(quantidadeAbsoluta); // Protegido pela exception do domínio!
        }

        // 4. Cria o registro de auditoria (Histórico)
        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque(
                command.produtoId(),
                tipo,
                quantidadeAbsoluta,
                command.observacao() != null ? command.observacao() : "Ajuste manual de inventário"
        );

        // 5. Salva prateleira e histórico de forma atômica
        estoqueGateway.salvar(saldo, movimentacao);
    }
}
