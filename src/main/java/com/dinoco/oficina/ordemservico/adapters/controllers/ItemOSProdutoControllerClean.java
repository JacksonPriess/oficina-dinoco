package com.dinoco.oficina.ordemservico.adapters.controllers;

import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemproduto.AdicionarItemProdutoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemproduto.AdicionarItemProdutoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemproduto.AlterarItemProdutoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemproduto.AlterarItemProdutoUseCase;

public class ItemOSProdutoControllerClean {

    private final AdicionarItemProdutoUseCase adicionarItemProdutoUseCase;
    private final AlterarItemProdutoUseCase alterarItemProdutoUseCase;

    public ItemOSProdutoControllerClean(AdicionarItemProdutoUseCase adicionarItemProdutoUseCase, AlterarItemProdutoUseCase alterarItemProdutoUseCase) {
        this.adicionarItemProdutoUseCase = adicionarItemProdutoUseCase;
        this.alterarItemProdutoUseCase = alterarItemProdutoUseCase;
    }

    public void adicionarItemProduto(AdicionarItemProdutoCommand command) {
        adicionarItemProdutoUseCase.executar(command);
    }

    public void alterarItemProduto(AlterarItemProdutoCommand command) {
        alterarItemProdutoUseCase.executar(command);
    }

}
