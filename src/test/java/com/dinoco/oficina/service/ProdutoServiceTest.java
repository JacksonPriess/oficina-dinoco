package com.dinoco.oficina.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import com.dinoco.oficina.entity.Produto;
import com.dinoco.oficina.dto.ProdutoResponseDto;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.repository.ProdutoRepository;
import com.dinoco.oficina.util.builders.ProdutoBuilder;
import com.dinoco.oficina.util.builders.ProdutoRequestDtoBuilder;
import com.dinoco.oficina.util.builders.ProdutoUpdateRequestDtoBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private MovimentacaoEstoqueService movimentacaoEstoqueService;

    @InjectMocks
    private ProdutoService service;

    @Captor
    private ArgumentCaptor<Produto> produtoCaptor;

    @Test
    @DisplayName("Deve criar produto SEM estoque inicial e NÃO registrar movimentação")
    void deveCriarProdutoSemEstoqueInicial() {
        var request = ProdutoRequestDtoBuilder.criarSemEstoqueInicial();
        var produtoMock = ProdutoBuilder.umProduto().comId(1L).comQuantidadeAtual(BigDecimal.ZERO).build();

        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoMock);

        ProdutoResponseDto response = service.criar(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);

        verify(produtoRepository).save(produtoCaptor.capture());
        assertThat(produtoCaptor.getValue().getQuantidadeAtual()).isEqualTo(BigDecimal.ZERO);
        // Garante que o serviço de estoque NUNCA foi chamado
        verify(movimentacaoEstoqueService, never()).registrarEntrada(any(), any(), any());
    }

    @Test
    @DisplayName("Deve criar produto COM estoque inicial e registrar movimentação")
    void deveCriarProdutoComEstoqueInicial() {
        BigDecimal estoqueInicial = new BigDecimal("50.000");
        var request = ProdutoRequestDtoBuilder.criarComEstoqueInicial(estoqueInicial);

        var produtoMock = ProdutoBuilder.umProduto().comId(2L).comQuantidadeAtual(estoqueInicial).build();

        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoMock);

        service.criar(request);

        verify(produtoRepository).save(any(Produto.class));

        // Garante que o serviço de estoque FOI chamado com a quantidade e o ID corretos
        verify(movimentacaoEstoqueService).registrarEntrada(
                eq(2L),
                eq(estoqueInicial),
                eq("Saldo inicial no cadastro do produto")
        );
    }

    @Test
    @DisplayName("Deve atualizar produto sem alterar estoque")
    void deveAtualizarProdutoSemAjusteDeEstoque() {
        Long id = 1L;
        Produto produtoExistente = ProdutoBuilder.umProduto().comQuantidadeAtual(new BigDecimal("10.000")).build();
        var request = ProdutoUpdateRequestDtoBuilder.criarSemAjusteDeEstoque(); // qtd atual vem null

        when(produtoRepository.findById(id)).thenReturn(Optional.of(produtoExistente));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoExistente);

        service.atualizar(id, request);

        verify(produtoRepository).save(produtoCaptor.capture());
        Produto atualizado = produtoCaptor.getValue();

        assertThat(atualizado.getNome()).isEqualTo("Pastilha de Freio Atualizada");
        assertThat(atualizado.getPrecoCusto()).isEqualTo(new BigDecimal("55.00")); // Atualizou preço

        // Garante que o serviço de ajuste de estoque NÃO foi chamado
        verify(movimentacaoEstoqueService, never()).ajustarInventario(any(), any(), any());
    }

    @Test
    @DisplayName("Deve atualizar produto e gerar ajuste de inventário positivo (acréscimo)")
    void deveAtualizarProdutoComAjusteDeEstoquePositivo() {
        Long id = 1L;
        Produto produtoExistente = ProdutoBuilder.umProduto().comQuantidadeAtual(new BigDecimal("10.000")).build();

        // Usuário alterou na tela de 10 para 15
        var request = ProdutoUpdateRequestDtoBuilder.criarAjusteDeEstoque(new BigDecimal("15.000"));

        when(produtoRepository.findById(id)).thenReturn(Optional.of(produtoExistente));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoExistente);

        service.atualizar(id, request);

        // A diferença deve ser +5.000
        verify(movimentacaoEstoqueService).ajustarInventario(
                eq(id),
                eq(new BigDecimal("5.000")),
                eq("Ajuste manual via edição de produto")
        );
    }

    @Test
    @DisplayName("Deve atualizar produto e gerar ajuste de inventário negativo (baixa)")
    void deveAtualizarProdutoComAjusteDeEstoqueNegativo() {
        Long id = 1L;
        Produto produtoExistente = ProdutoBuilder.umProduto().comQuantidadeAtual(new BigDecimal("20.000")).build();
        // Usuário alterou na tela de 20 para 12
        var request = ProdutoUpdateRequestDtoBuilder.criarAjusteDeEstoque(new BigDecimal("12.000"));
        when(produtoRepository.findById(id)).thenReturn(Optional.of(produtoExistente));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoExistente);
        service.atualizar(id, request);
        // A diferença deve ser -8.000
        verify(movimentacaoEstoqueService).ajustarInventario(
                eq(id),
                eq(new BigDecimal("-8.000")),
                eq("Ajuste manual via edição de produto")
        );
    }

    @Test
    @DisplayName("Deve listar todos os produtos quando termo for nulo ou vazio")
    void deveListarTodos() {
        Produto p = ProdutoBuilder.umProduto().build();
        when(produtoRepository.findAll()).thenReturn(List.of(p));

        List<ProdutoResponseDto> result = service.listar(null);

        assertThat(result).hasSize(1);
        verify(produtoRepository).findAll();
        verify(produtoRepository, never()).buscaAvancada(anyString());
    }

    @Test
    @DisplayName("Deve listar produtos através de busca avançada quando termo for informado")
    void deveListarComBuscaAvancada() {
        Produto p = ProdutoBuilder.umProduto().build();
        String termo = "Filtro";
        when(produtoRepository.buscaAvancada(termo)).thenReturn(List.of(p));

        List<ProdutoResponseDto> result = service.listar(termo);

        assertThat(result).hasSize(1);
        verify(produtoRepository).buscaAvancada(termo);
        verify(produtoRepository, never()).findAll();
    }

    @Test
    @DisplayName("Deve buscar entidade produto por ID com sucesso")
    void deveBuscarEntidadePorId() {
        Long id = 1L;
        Produto p = ProdutoBuilder.umProduto().comId(id).build();
        when(produtoRepository.findById(id)).thenReturn(Optional.of(p));

        Produto resultado = service.buscarEntidadePorId(id);

        assertThat(resultado.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar buscar produto inexistente")
    void deveLancarExcecaoAoBuscarProdutoInexistente() {
        Long id = 99L;
        when(produtoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarEntidadePorId(id));
        assertThrows(RecursoNaoEncontradoException.class, () -> service.atualizar(id, ProdutoUpdateRequestDtoBuilder.criarSemAjusteDeEstoque()));
        assertThrows(RecursoNaoEncontradoException.class, () -> service.desativar(id));
    }

    @Test
    @DisplayName("Deve inativar produto com sucesso")
    void deveDesativarProduto() {
        Long id = 1L;
        Produto produto = ProdutoBuilder.umProduto().build();
        produto.setAtivo(true);

        when(produtoRepository.findById(id)).thenReturn(Optional.of(produto));

        service.desativar(id);

        verify(produtoRepository).save(produtoCaptor.capture());
        assertThat(produtoCaptor.getValue().isAtivo()).isFalse();
    }
}