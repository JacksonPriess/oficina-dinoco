package com.dinoco.oficina.estoque.application.gateways;

import com.dinoco.oficina.shared.events.ProdutoCadastradoEvent;

public interface InicializacaoEstoqueGateway {
    // Deve participar da mesma transação que salva saldo e movimentação.
    boolean registrarSeNovo(ProdutoCadastradoEvent evento);
}

