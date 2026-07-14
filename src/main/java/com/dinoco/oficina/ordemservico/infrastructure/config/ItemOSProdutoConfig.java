package com.dinoco.oficina.ordemservico.infrastructure.config;

import com.dinoco.oficina.ordemservico.adapters.controllers.ItemOSProdutoControllerClean;
import com.dinoco.oficina.ordemservico.application.gateways.CatalogoProdutoGateway;
import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemproduto.AdicionarItemProdutoHandler;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemproduto.AdicionarItemProdutoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemproduto.AlterarItemProdutoHandler;
import com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemproduto.AlterarItemProdutoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ItemOSProdutoConfig {

    @Bean
    public AdicionarItemProdutoUseCase adicionarItemProdutoUseCase(OrdemServicoCommandGateway ordemServicoCommandGateway, CatalogoProdutoGateway catalogoProdutoGateway) {
        return new AdicionarItemProdutoHandler(ordemServicoCommandGateway, catalogoProdutoGateway);
    }

    @Bean
    public AlterarItemProdutoUseCase alterarItemProdutoUseCase(OrdemServicoCommandGateway ordemServicoCommandGateway, CatalogoProdutoGateway catalogoProdutoGateway) {
        return new AlterarItemProdutoHandler(ordemServicoCommandGateway);
    }

    @Bean
    public ItemOSProdutoControllerClean itemOSProdutoControllerClean(
            AdicionarItemProdutoUseCase adicionarItemProdutoUseCase,
            AlterarItemProdutoUseCase alterarItemProdutoUseCase) {
        return new ItemOSProdutoControllerClean(adicionarItemProdutoUseCase, alterarItemProdutoUseCase);
    }
}