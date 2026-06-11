package com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemproduto;

import com.dinoco.oficina.ordemservico.application.gateways.CatalogoProdutoGateway;
import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.domain.exceptions.RecursoNaoEncontradoException;
import com.dinoco.oficina.ordemservico.domain.models.ItemOSProduto;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;

import java.math.BigDecimal;

public class AdicionarItemProdutoHandler implements AdicionarItemProdutoUseCase{

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;
    private final CatalogoProdutoGateway catalogoProdutoGateway;

    public AdicionarItemProdutoHandler(OrdemServicoCommandGateway ordemServicoCommandGateway, CatalogoProdutoGateway catalogoProdutoGateway) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
        this.catalogoProdutoGateway = catalogoProdutoGateway;
    }

    @Override
    public void executar(AdicionarItemProdutoCommand command) {
        // 1. Busca a Ordem de Serviço (Domínio Rico)
        OrdemServico os = ordemServicoCommandGateway.buscarParaAlteracao(command.osId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada."));

        // 2. Consulta o preço atual do produto no outro módulo
        BigDecimal precoVenda = catalogoProdutoGateway.buscarPrecoVendaAtual(command.produtoId());

        // 3. Cria a entidade filha
        ItemOSProduto novoItem = new ItemOSProduto(command.produtoId(), command.quantidade(), precoVenda);

        // 4. Aplica a inteligência do negócio
        os.adicionarProduto(novoItem);

        // 5. Salva apenas o Agregado Raiz! O Hibernate faz o INSERT na tabela filha pelo Cascade.
        ordemServicoCommandGateway.salvar(os);
    }
}
