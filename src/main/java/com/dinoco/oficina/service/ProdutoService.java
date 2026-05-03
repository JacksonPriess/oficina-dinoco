package com.dinoco.oficina.service;

import com.dinoco.oficina.dto.ProdutoRequestDto;
import com.dinoco.oficina.dto.ProdutoResponseDto;
import com.dinoco.oficina.dto.ProdutoUpdateRequestDto;
import com.dinoco.oficina.entity.Produto;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;
    private static final String MSG_PRODUTO_NAO_ENCONTRADO = "Produto não encontrado.";

    @Transactional
    public ProdutoResponseDto criar(ProdutoRequestDto dto) {

        Produto produto = new Produto();
        produto.setNome(dto.nome());
        produto.setTipo(dto.tipo());
        produto.setMarca(dto.marca());
        produto.setCodigoFabricante(dto.codigoFabricante());
        produto.setAplicacao(dto.aplicacao());
        produto.setQuantidadeAtual(BigDecimal.ZERO);
        produto.setQuantidadeReservada(BigDecimal.ZERO);
        produto.setPrecoCusto(dto.precoCusto());
        produto.setPrecoVenda(dto.precoVenda());
        Produto produtoSalvo = produtoRepository.save(produto);
        if (dto.quantidadeAtual().compareTo(BigDecimal.ZERO) > 0) {
            movimentacaoEstoqueService.registrarEntrada(produtoSalvo.getId(),dto.quantidadeAtual(), "Saldo inicial no cadastro do produto");
        }
        return mapearParaResponse(produtoSalvo);
    }

    @Transactional
    public ProdutoResponseDto atualizar(Long id, ProdutoUpdateRequestDto dto) {

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(MSG_PRODUTO_NAO_ENCONTRADO));

        boolean houveAjusteEstoque = dto.quantidadeAtual() != null && dto.quantidadeAtual().compareTo(produto.getQuantidadeAtual()) != 0;
        BigDecimal diferencaEstoque = BigDecimal.ZERO;
        if (houveAjusteEstoque) {
            diferencaEstoque = dto.quantidadeAtual().subtract(produto.getQuantidadeAtual());
        }
        produto.setNome(dto.nome());
        produto.setTipo(dto.tipo());
        produto.setMarca(dto.marca());
        produto.setCodigoFabricante(dto.codigoFabricante());
        produto.setAplicacao(dto.aplicacao());
        produto.setPrecoCusto(dto.precoCusto() != null ? dto.precoCusto() : produto.getPrecoCusto());
        produto.setPrecoVenda(dto.precoVenda() != null ? dto.precoVenda() : produto.getPrecoVenda());
        Produto salvo = produtoRepository.save(produto);
        if (houveAjusteEstoque) {
            movimentacaoEstoqueService.ajustarInventario(id, diferencaEstoque, "Ajuste manual via edição de produto");
        }
        return mapearParaResponse(salvo);
    }

    public List<ProdutoResponseDto> listar(String termo) {
        List<Produto> produtos = (termo != null && !termo.isBlank())
                ? produtoRepository.buscaAvancada(termo)
                : produtoRepository.findAll();

        return produtos.stream().map(this::mapearParaResponse).toList();
    }

    @Transactional
    public void desativar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(MSG_PRODUTO_NAO_ENCONTRADO));
        produto.setAtivo(false);
        produtoRepository.save(produto);
    }

    public Produto buscarEntidadePorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado com ID: " + id));
    }

    private ProdutoResponseDto mapearParaResponse(Produto p) {
        return new ProdutoResponseDto(
                p.getId(),
                p.getNome(),
                p.getTipo(),
                p.getMarca(),
                p.getCodigoFabricante(),
                p.getAplicacao(),
                p.getQuantidadeAtual(),
                p.getQuantidadeReservada(),
                p.getPrecoVenda(),
                p.getPrecoCusto(),
                p.isAtivo()
        );
    }
}