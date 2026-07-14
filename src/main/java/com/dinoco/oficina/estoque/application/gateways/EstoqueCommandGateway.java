package com.dinoco.oficina.estoque.application.gateways;

import com.dinoco.oficina.estoque.domain.MovimentacaoEstoque;
import com.dinoco.oficina.estoque.domain.SaldoEstoque;

import java.util.Optional;

public interface EstoqueCommandGateway {

    void salvar(SaldoEstoque saldo, MovimentacaoEstoque movimentacao);

    Optional<SaldoEstoque> buscarSaldoPorProdutoIdParaAlteracao(Long produtoId);

}
