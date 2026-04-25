package com.dinoco.oficina.service;

import com.dinoco.oficina.dto.ProdutoRequestDto;
import com.dinoco.oficina.dto.ProdutoResponseDto;
import com.dinoco.oficina.dto.ProdutoUpdateRequestDto;
import com.dinoco.oficina.entity.Cliente;
import com.dinoco.oficina.entity.Produto;
import com.dinoco.oficina.enums.TipoMovimentacao;
import com.dinoco.oficina.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
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

    @Transactional
    public ProdutoResponseDto criar(ProdutoRequestDto dto) {

        Produto produto = new Produto();
        produto.setNome(dto.nome());
        produto.setTipo(dto.tipo());
        produto.setMarca(dto.marca());
        produto.setCodigoFabricante(dto.codigoFabricante());
        produto.setAplicacao(dto.aplicacao());
        produto.setQuantidadeAtual(dto.quantidadeAtual());
        produto.setQuantidadeReservada(dto.quantidadeReservada());
        produto.setPrecoCusto(dto.precoCusto());
        produto.setPrecoVenda(dto.precoVenda());
        Produto salvo = produtoRepository.save(produto);

        //Na criação do produto, quando houve entrada real, deve movimentar estoque
        if (dto.quantidadeAtual().compareTo(BigDecimal.ZERO) > 0) {
            movimentacaoEstoqueService.registrarMovimentacao(salvo, dto.quantidadeAtual(),
                    TipoMovimentacao.ENTRADA_FORNECEDOR, "Carga inicial de estoque");
        }

        return mapearParaResponse(salvo);
    }

    @Transactional
    public ProdutoResponseDto atualizar(Long id, ProdutoUpdateRequestDto dto) {

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        produto.setNome(dto.nome());
        produto.setTipo(dto.tipo());
        produto.setMarca(dto.marca());
        produto.setCodigoFabricante(dto.codigoFabricante());
        produto.setAplicacao(dto.aplicacao());

        produto.setQuantidadeAtual(dto.quantidadeAtual() != null ? dto.quantidadeAtual() : produto.getQuantidadeAtual());
        produto.setQuantidadeReservada(dto.quantidadeReservada() != null ? dto.quantidadeReservada() : produto.getQuantidadeReservada());
        produto.setPrecoCusto(dto.precoCusto() != null ? dto.precoCusto() : produto.getPrecoCusto());
        produto.setPrecoVenda(dto.precoVenda() != null ? dto.precoVenda() : produto.getPrecoVenda());

        Produto salvo = produtoRepository.save(produto);

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
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        produto.setAtivo(false);
        produtoRepository.save(produto);
    }

    public Produto buscarEntidadePorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + id));
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
