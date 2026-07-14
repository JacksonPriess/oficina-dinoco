package com.dinoco.oficina.ordemservico.application.usecases.commands.abrir;

import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.ordemservico.application.gateways.CatalogoProdutoGateway;
import com.dinoco.oficina.ordemservico.application.gateways.CatalogoServicoGateway;
import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.domain.models.ItemOSProduto;
import com.dinoco.oficina.ordemservico.domain.models.ItemOSServico;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import com.dinoco.oficina.cliente.application.gateways.ClienteQueryGateway;

import java.math.BigDecimal;
import java.util.Optional;

public class AbrirOrdemServicoHandler implements AbrirOrdemServicoUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;
    private final ClienteQueryGateway clienteQueryGateway;
    private final CatalogoProdutoGateway catalogoProdutoGateway;
    private final CatalogoServicoGateway catalogoServicoGateway;

    public AbrirOrdemServicoHandler(OrdemServicoCommandGateway ordemServicoCommandGateway,
                                    ClienteQueryGateway clienteQueryGateway,
                                    CatalogoProdutoGateway catalogoProdutoGateway,
                                    CatalogoServicoGateway catalogoServicoGateway) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
        this.clienteQueryGateway = clienteQueryGateway;
        this.catalogoProdutoGateway = catalogoProdutoGateway;
        this.catalogoServicoGateway = catalogoServicoGateway;
    }

    public AbrirOrdemServicoOutput executar(AbrirOrdemServicoCommand command) {

        OrdemServico novaOs = new OrdemServico(
                command.clienteId(),
                command.veiculoId(),
                command.quilometragemEntrada(),
                command.reclamacaoCliente()
        );

        if (command.produtos() != null && !command.produtos().isEmpty()) {
            command.produtos().forEach(itemProdutoCommand -> {
                BigDecimal precoVenda = catalogoProdutoGateway.buscarPrecoVendaAtual(itemProdutoCommand.produtoId());
                if (precoVenda == null) {
                    throw new RecursoNaoEncontradoException("Produto com ID " + itemProdutoCommand.produtoId() + " não encontrado.");
                }
                ItemOSProduto itemOSProduto = new ItemOSProduto(itemProdutoCommand.produtoId(), itemProdutoCommand.quantidade(), precoVenda);
                novaOs.adicionarProduto(itemOSProduto);
            });
        }

        if (command.servicos() != null && !command.servicos().isEmpty()) {
            command.servicos().forEach(itemServicoCommand -> {
                Optional<BigDecimal> precoPadraoOptional = catalogoServicoGateway.buscarPrecoPadrao(itemServicoCommand.servicoId());
                BigDecimal precoPadrao = precoPadraoOptional.orElseThrow(() -> new RecursoNaoEncontradoException("Serviço com ID " + itemServicoCommand.servicoId() + " não encontrado."));
                ItemOSServico itemOSServico = new ItemOSServico(itemServicoCommand.servicoId(), itemServicoCommand.mecanicoId(), precoPadrao);
                novaOs.adicionarServico(itemOSServico);
            });
        }

        OrdemServico osSalva = ordemServicoCommandGateway.salvar(novaOs);

        return new AbrirOrdemServicoOutput(
                osSalva.getId(),
                osSalva.getCodigoRastreio(),
                osSalva.getClienteId(),
                osSalva.getVeiculoId(),
                osSalva.getStatus(),
                osSalva.getReclamacaoCliente(),
                osSalva.getQuilometragemEntrada(),
                osSalva.getValorTotalServicos(),
                osSalva.getValorTotalProdutos(),
                osSalva.getValorTotalOS(),
                osSalva.getValorDesconto()
        );
    }
}