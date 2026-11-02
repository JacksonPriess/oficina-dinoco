package com.dinoco.oficina.estoque.application.usecases.commands.inicializar;

import com.dinoco.oficina.shared.events.ProdutoCadastradoEvent;

public interface InicializarEstoqueUseCase {
    void executar(ProdutoCadastradoEvent evento);
}
