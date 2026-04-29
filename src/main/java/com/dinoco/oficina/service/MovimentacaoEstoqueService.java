package com.dinoco.oficina.service;

import com.dinoco.oficina.entity.ItemOSProduto;
import com.dinoco.oficina.entity.MovimentacaoEstoque;
import com.dinoco.oficina.entity.OrdemServico;
import com.dinoco.oficina.entity.Produto;
import com.dinoco.oficina.enums.TipoMovimentacao;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.repository.MovimentacaoEstoqueRepository;
import com.dinoco.oficina.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;

    private void registrarMovimentacao(Produto p, BigDecimal qtd, TipoMovimentacao tipo, String obs) {
        MovimentacaoEstoque mov = MovimentacaoEstoque.builder()
                .produto(p)
                .tipoMovimentacao(tipo)
                .quantidade(qtd)
                .dataMovimentacao(LocalDateTime.now())
                .observacao(obs)
                .build();
        movimentacaoRepository.save(mov);
    }

    @Transactional
    public void reservarItens(OrdemServico os) {
        for (ItemOSProduto item : os.getItensProduto()) {
            Produto produto = item.getProduto();
            produto.adicionarQuantidadeReservada(item.getQuantidade());
            produtoRepository.save(produto);
            registrarMovimentacao(produto, item.getQuantidade(), TipoMovimentacao.RESERVA_OS, "Reserva OS: " + os.getCodigoRastreio());
        }
    }

    @Transactional
    public void consumirReservasParaExecucao(OrdemServico os) {
        for (ItemOSProduto item : os.getItensProduto()) {
            Produto produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado no estoque"));
            produto.consumirQuantidadeReservadaEFisica(item.getQuantidade());
            produtoRepository.save(produto);
            registrarMovimentacao(produto,item.getQuantidade(),TipoMovimentacao.BAIXA_EXECUCAO_OS,"Baixa física OS: " + os.getCodigoRastreio());
        }
    }

    @Transactional
    public void registrarEntrada(Long produtoId, BigDecimal quantidade, String observacao) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));
        produto.atualizarQuantidadeReal(quantidade);
        produtoRepository.save(produto);
        registrarMovimentacao(produto, quantidade, TipoMovimentacao.ENTRADA, observacao);
    }

    @Transactional
    public void ajustarInventario(Long produtoId, BigDecimal diferenca, String observacao) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));

        produto.atualizarQuantidadeReal(diferenca);
        TipoMovimentacao tipo = diferenca.compareTo(BigDecimal.ZERO) > 0
                ? TipoMovimentacao.AJUSTE_ENTRADA
                : TipoMovimentacao.AJUSTE_SAIDA;
        produtoRepository.save(produto);

        BigDecimal quantidadeMovimento = diferenca.abs();
        registrarMovimentacao(produto, quantidadeMovimento, tipo, observacao);
    }
}