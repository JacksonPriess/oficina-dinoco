package com.dinoco.oficina.service;

import com.dinoco.oficina.entity.ItemOSProduto;
import com.dinoco.oficina.entity.MovimentacaoEstoque;
import com.dinoco.oficina.entity.OrdemServico;
import com.dinoco.oficina.entity.Produto;
import com.dinoco.oficina.enums.TipoMovimentacao;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.repository.MovimentacaoEstoqueRepository;
import com.dinoco.oficina.repository.ProdutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimentacaoEstoqueServiceTest {

    @InjectMocks
    private MovimentacaoEstoqueService movimentacaoEstoqueService;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private MovimentacaoEstoqueRepository movimentacaoRepository;

    @Captor
    private ArgumentCaptor<MovimentacaoEstoque> movCaptor;

    @Test
    @DisplayName("Deve reservar itens, atualizar quantidade reservada do produto e registrar movimentação")
    void deveReservarItensEGerarMovimentacao() {
        // 1. Arrange
        OrdemServico os = new OrdemServico();
        os.setCodigoRastreio("OS-12345");

        Produto produto = new Produto();
        produto.setQuantidadeReservada(new BigDecimal("10.00"));

        ItemOSProduto item = new ItemOSProduto();
        item.setProduto(produto);
        item.setQuantidade(new BigDecimal("5.00"));
        os.setItensProduto(Set.of(item));

        // 2. Act
        movimentacaoEstoqueService.reservarItens(os);

        // 3. Assert
        assertEquals(new BigDecimal("15.00"), produto.getQuantidadeReservada());

        // Verifica se o produto atualizado foi salvo
        verify(produtoRepository, times(1)).save(produto);

        // Valida o registro da movimentação de estoque (o log)
        verify(movimentacaoRepository, times(1)).save(movCaptor.capture());
        MovimentacaoEstoque mov = movCaptor.getValue();

        assertEquals(TipoMovimentacao.RESERVA_OS, mov.getTipoMovimentacao());
        assertEquals(new BigDecimal("5.00"), mov.getQuantidade());
        assertTrue(mov.getObservacao().contains("OS-12345"));
        assertNotNull(mov.getDataMovimentacao());
    }

    @Test
    @DisplayName("Deve consumir quantidade física e reservada do produto e gerar log de baixa por execução")
    void deveConsumirReservasEGerarMovimentacao() {
        // 1. Arrange
        //Simula um produto
        Long produtoId = 100L;
        Produto produtoNoBanco = new Produto();
        produtoNoBanco.setId(produtoId);
        produtoNoBanco.setQuantidadeAtual(new BigDecimal("20.00"));
        produtoNoBanco.setQuantidadeReservada(new BigDecimal("2.00")); // Tinha 5 reservados

        //Simula um item de produto
        ItemOSProduto item = new ItemOSProduto();
        //Define a quantidade de peças, na aprovação soma-se a quantidade em reserva, aqui vai subtrair.
        item.setQuantidade(new BigDecimal("2.00"));

        // Para o teste do estoque, precisamos de uma instância do produto atrelada ao item
        Produto produtoDoItem = new Produto();
        produtoDoItem.setId(produtoId);
        item.setProduto(produtoDoItem);

        OrdemServico os = new OrdemServico();
        os.setCodigoRastreio("OS-99999");
        os.setItensProduto(Set.of(item));

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produtoNoBanco));

        // 2. Act
        movimentacaoEstoqueService.consumirReservasParaExecucao(os);

        // 3. Assert
        assertEquals(new BigDecimal("18.00"), produtoNoBanco.getQuantidadeAtual());
        // Reservada: 2 - 2 = 0
        assertEquals(new BigDecimal("0.00"), produtoNoBanco.getQuantidadeReservada());

        verify(produtoRepository, times(1)).save(produtoNoBanco);
        verify(movimentacaoRepository, times(1)).save(movCaptor.capture());
        MovimentacaoEstoque movimentacaoEstoque = movCaptor.getValue();
        assertEquals(TipoMovimentacao.BAIXA_EXECUCAO_OS, movimentacaoEstoque.getTipoMovimentacao());
        assertEquals(new BigDecimal("2.00"), movimentacaoEstoque.getQuantidade());
        assertTrue(movimentacaoEstoque.getObservacao().contains("OS-99999"));
    }

    @Test
    @DisplayName("Deve lançar exception quando tentar consumir mais peças do que o reservado")
    void deveLancarExceptionAoConsumirMaisQueOReservado() {
        // 1. Arrange
        Long produtoId = 100L;
        Produto produtoNoBanco = new Produto();
        produtoNoBanco.setId(produtoId);
        produtoNoBanco.setQuantidadeAtual(new BigDecimal("10.00"));
        produtoNoBanco.setQuantidadeReservada(new BigDecimal("1.00")); // Só tem 1 reservado

        ItemOSProduto item = new ItemOSProduto();
        item.setQuantidade(new BigDecimal("5.00")); // Tentando consumir 5

        Produto produtoDoItem = new Produto();
        produtoDoItem.setId(produtoId);
        item.setProduto(produtoDoItem);

        OrdemServico os = new OrdemServico();
        os.setItensProduto(Set.of(item));

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produtoNoBanco));

        // 2. Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> movimentacaoEstoqueService.consumirReservasParaExecucao(os)
        );

        assertEquals("Tentativa de consumir uma quantidade maior do que a reservada.", exception.getMessage());

        // Garante que nada foi salvo se a regra falhou
        verify(produtoRepository, never()).save(any(Produto.class));
        verify(movimentacaoRepository, never()).save(any(MovimentacaoEstoque.class));
    }

    @Test
    @DisplayName("Deve registrar entrada no estoque com sucesso e gerar movimentação de ENTRADA")
    void deveRegistrarEntradaComSucesso() {
        // 1. Arrange
        Long produtoId = 1L;
        BigDecimal quantidadeEntrada = new BigDecimal("50.00");
        String observacao = "Nota Fiscal 12345";

        Produto produtoMock = new Produto();
        produtoMock.setId(produtoId);
        produtoMock.setQuantidadeAtual(new BigDecimal("10.00"));

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produtoMock));

        // 2. Act
        movimentacaoEstoqueService.registrarEntrada(produtoId, quantidadeEntrada, observacao);

        // 3. Assert
        verify(produtoRepository, times(1)).save(produtoMock);
        verify(movimentacaoRepository, times(1)).save(movCaptor.capture());
        MovimentacaoEstoque mov = movCaptor.getValue();

        assertEquals(TipoMovimentacao.ENTRADA, mov.getTipoMovimentacao());
        assertEquals(quantidadeEntrada, mov.getQuantidade());
        assertEquals(observacao, mov.getObservacao());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao registrar entrada de produto inexistente")
    void deveLancarExceptionAoRegistrarEntradaSemProduto() {
        // 1. Arrange
        Long produtoIdInvalido = 99L;
        when(produtoRepository.findById(produtoIdInvalido)).thenReturn(Optional.empty());

        // 2. Act & Assert
        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> movimentacaoEstoqueService.registrarEntrada(produtoIdInvalido, BigDecimal.TEN, "Obs")
        );

        assertEquals("Produto não encontrado", exception.getMessage());

        verify(produtoRepository, never()).save(any());
        verify(movimentacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve realizar ajuste de inventário positivo e registrar como AJUSTE_ENTRADA")
    void deveRealizarAjusteInventarioPositivo() {
        // 1. Arrange
        Long produtoId = 1L;
        // O sistema contou 5 a mais no estoque físico do que constava no sistema
        BigDecimal diferencaPositiva = new BigDecimal("5.00");
        String observacao = "Sobra identificada no balanço anual";

        Produto produtoMock = new Produto();
        produtoMock.setId(produtoId);
        produtoMock.setQuantidadeAtual(new BigDecimal("10.00"));

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produtoMock));

        // 2. Act
        movimentacaoEstoqueService.ajustarInventario(produtoId, diferencaPositiva, observacao);

        // 3. Assert
        verify(produtoRepository, times(1)).save(produtoMock);

        verify(movimentacaoRepository, times(1)).save(movCaptor.capture());
        MovimentacaoEstoque mov = movCaptor.getValue();

        // Como a diferença é > 0, o tipo tem que ser AJUSTE_ENTRADA
        assertEquals(TipoMovimentacao.AJUSTE_ENTRADA, mov.getTipoMovimentacao());
        assertEquals(new BigDecimal("5.00"), mov.getQuantidade());
        assertEquals(observacao, mov.getObservacao());
    }

    @Test
    @DisplayName("Deve realizar ajuste de inventário negativo e registrar como AJUSTE_SAIDA em módulo absoluto")
    void deveRealizarAjusteInventarioNegativo() {
        // 1. Arrange
        Long produtoId = 1L;
        BigDecimal diferencaNegativa = new BigDecimal("-3.00");
        String observacao = "Quebra identificada no balanço anual";
        Produto produtoMock = new Produto();
        produtoMock.setId(produtoId);
        produtoMock.setQuantidadeAtual(new BigDecimal("10.00"));
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produtoMock));
        // 2. Act
        movimentacaoEstoqueService.ajustarInventario(produtoId, diferencaNegativa, observacao);
        // 3. Assert
        verify(movimentacaoRepository, times(1)).save(movCaptor.capture());
        MovimentacaoEstoque mov = movCaptor.getValue();
        assertEquals(TipoMovimentacao.AJUSTE_SAIDA, mov.getTipoMovimentacao());
        assertEquals(new BigDecimal("3.00"), mov.getQuantidade());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao tentar ajustar inventário de produto inexistente")
    void deveLancarExceptionAoAjustarInventarioSemProduto() {
        // 1. Arrange
        Long produtoIdInvalido = 99L;
        when(produtoRepository.findById(produtoIdInvalido)).thenReturn(Optional.empty());
        // 2. Act & Assert
        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> movimentacaoEstoqueService.ajustarInventario(produtoIdInvalido, BigDecimal.ONE, "Obs")
        );
        assertEquals("Produto não encontrado", exception.getMessage());
        verify(produtoRepository, never()).save(any());
        verify(movimentacaoRepository, never()).save(any());
    }
}