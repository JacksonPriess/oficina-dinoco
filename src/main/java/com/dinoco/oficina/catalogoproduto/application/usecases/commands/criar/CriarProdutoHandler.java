package com.dinoco.oficina.catalogoproduto.application.usecases.commands.criar;

import com.dinoco.oficina.catalogoproduto.application.gateways.ProdutoCommandGateway;
import com.dinoco.oficina.catalogoproduto.application.gateways.ProdutoQueryGateway;
import com.dinoco.oficina.catalogoproduto.domain.Produto;

public class CriarProdutoHandler implements CriarProdutoUseCase {

    private final ProdutoCommandGateway produtoCommandGateway;
    private final ProdutoQueryGateway produtoQueryGateway;

    public CriarProdutoHandler(ProdutoCommandGateway produtoCommandGateway, ProdutoQueryGateway produtoQueryGateway) {
        this.produtoCommandGateway = produtoCommandGateway;
        this.produtoQueryGateway = produtoQueryGateway;
    }

    @Override
    public CriarProdutoOutput executar(CriarProdutoCommand command) {

        if (produtoQueryGateway.existePorNome(command.nome())) {
            throw new IllegalArgumentException("Produto já cadastrado com este nome.");
        }

        Produto novoProduto = new Produto(
                command.nome(),
                command.tipo(),
                command.marca(),
                command.codigoFabricante(),
                command.aplicacao(),
                command.quantidadeAtual(),
                command.quantidadeReservada(),
                command.precoCusto(),
                command.precoVenda()
        );

        Produto produtoSalvo = produtoCommandGateway.salvar(novoProduto);

        return mapearParaOutput(produtoSalvo);
    }

    private CriarProdutoOutput mapearParaOutput(Produto produto) {

        return new CriarProdutoOutput(
                produto.getId(),
                produto.getVersao(),
                produto.getNome(),
                produto.getTipo(),
                produto.getMarca(),
                produto.getCodigoFabricante(),
                produto.getAplicacao(),
                produto.getQuantidadeAtual(),
                produto.getQuantidadeReservada(),
                produto.getPrecoCusto(),
                produto.getPrecoVenda(),
                produto.getAtivo()
        );
    }
}