package com.dinoco.oficina.service;

import com.dinoco.oficina.dto.ProdutoRequestDto;
import com.dinoco.oficina.dto.ProdutoResponseDto;
import com.dinoco.oficina.entity.Produto;
import com.dinoco.oficina.enums.TipoMovimentacao;
import com.dinoco.oficina.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        produto.setPrecoCusto(dto.precoCusto());
        produto.setPrecoVenda(dto.precoVenda());
        produto.setQuantidadeAtual(dto.quantidadeInicial());

        Produto salvo = produtoRepository.save(produto);

        // Se houver quantidade inicial, registra a movimentação de entrada
        if (dto.quantidadeInicial().compareTo(java.math.BigDecimal.ZERO) > 0) {
            movimentacaoEstoqueService.registrarMovimentacao(salvo, dto.quantidadeInicial(),
                    TipoMovimentacao.ENTRADA_FORNECEDOR, "Carga inicial de estoque");
        }

        return mapearParaResponse(salvo);
    }

    public List<ProdutoResponseDto> listar(String termo) {
        List<Produto> produtos = (termo != null && !termo.isBlank())
                ? produtoRepository.buscaAvancada(termo)
                : produtoRepository.findAll();

        return produtos.stream().map(this::mapearParaResponse).toList();
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
                p.getQuantidadeDisponivel(),
                p.getPrecoVenda(),
                p.isAtivo()
        );
    }
}
