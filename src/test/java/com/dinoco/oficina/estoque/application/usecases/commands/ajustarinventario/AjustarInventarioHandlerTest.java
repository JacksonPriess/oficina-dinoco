package com.dinoco.oficina.estoque.application.usecases.commands.ajustarinventario;

import com.dinoco.oficina.estoque.application.gateways.EstoqueCommandGateway;
import com.dinoco.oficina.estoque.domain.MovimentacaoEstoque;
import com.dinoco.oficina.estoque.domain.SaldoEstoque;
import com.dinoco.oficina.estoque.domain.TipoMovimentacao;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AjustarInventarioHandlerTest {

	@InjectMocks
	private AjustarInventarioHandler handler;

	@Mock
	private EstoqueCommandGateway estoqueGateway;

	@Captor
	private ArgumentCaptor<SaldoEstoque> saldoCaptor;

	@Captor
	private ArgumentCaptor<MovimentacaoEstoque> movCaptor;

	@Test
	@DisplayName("Deve ajustar o inventário para mais (AJUSTE_ENTRADA) e salvar movimentação com observação padrão quando nula")
	void deveAjustarEntradaQuandoQuantidadeContadaMaior() {
		// Arrange
		Long produtoId = 1L;
		SaldoEstoque saldo = new SaldoEstoque(10L, produtoId, new BigDecimal("5.00"), BigDecimal.ZERO, 2L);

		when(estoqueGateway.buscarSaldoPorProdutoIdParaAlteracao(produtoId)).thenReturn(Optional.of(saldo));

		AjustarInventarioCommand command = new AjustarInventarioCommand(produtoId, 2L, new BigDecimal("8.00"), null);

		// Act
		handler.executar(command);

		// Assert
		verify(estoqueGateway, times(1)).salvar(saldoCaptor.capture(), movCaptor.capture());

		SaldoEstoque saldoSalvo = saldoCaptor.getValue();
		MovimentacaoEstoque mov = movCaptor.getValue();

		assertEquals(new BigDecimal("8.00"), saldoSalvo.getQuantidadeReal());
		assertEquals(TipoMovimentacao.AJUSTE_ENTRADA, mov.getTipo());
		assertEquals(new BigDecimal("3.00"), mov.getQuantidade());
		assertEquals("Ajuste manual de inventário", mov.getObservacao());
	}

	@Test
	@DisplayName("Deve ajustar o inventário para menos (AJUSTE_SAIDA) e salvar movimentação com observação informada")
	void deveAjustarSaidaQuandoQuantidadeContadaMenor() {
		// Arrange
		Long produtoId = 2L;
		SaldoEstoque saldo = new SaldoEstoque(11L, produtoId, new BigDecimal("10.00"), BigDecimal.ZERO, 1L);

		when(estoqueGateway.buscarSaldoPorProdutoIdParaAlteracao(produtoId)).thenReturn(Optional.of(saldo));

		AjustarInventarioCommand command = new AjustarInventarioCommand(produtoId, 1L, new BigDecimal("7.00"), "Quebra anual");

		// Act
		handler.executar(command);

		// Assert
		verify(estoqueGateway, times(1)).salvar(saldoCaptor.capture(), movCaptor.capture());

		SaldoEstoque saldoSalvo = saldoCaptor.getValue();
		MovimentacaoEstoque mov = movCaptor.getValue();

		assertEquals(new BigDecimal("7.00"), saldoSalvo.getQuantidadeReal());
		assertEquals(TipoMovimentacao.AJUSTE_SAIDA, mov.getTipo());
		assertEquals(new BigDecimal("3.00"), mov.getQuantidade());
		assertEquals("Quebra anual", mov.getObservacao());
	}

	@Test
	@DisplayName("Deve lançar IllegalArgumentException quando não encontrar saldo para o produto")
	void deveLancarQuandoSaldoNaoEncontrado() {
		// Arrange
		Long produtoId = 3L;
		when(estoqueGateway.buscarSaldoPorProdutoIdParaAlteracao(produtoId)).thenReturn(Optional.empty());

		AjustarInventarioCommand command = new AjustarInventarioCommand(produtoId, 0L, BigDecimal.ONE, null);

		// Act & Assert
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> handler.executar(command));
		assertEquals("Prateleira não encontrada para este produto.", ex.getMessage());
		verify(estoqueGateway, never()).salvar(any(), any());
	}

	@Test
	@DisplayName("Deve lançar IllegalStateException quando versão do saldo for diferente da versão informada no comando")
	void deveLancarQuandoVersaoDiferente() {
		// Arrange
		Long produtoId = 4L;
		SaldoEstoque saldo = new SaldoEstoque(12L, produtoId, BigDecimal.TEN, BigDecimal.ZERO, 5L);
		when(estoqueGateway.buscarSaldoPorProdutoIdParaAlteracao(produtoId)).thenReturn(Optional.of(saldo));

		AjustarInventarioCommand command = new AjustarInventarioCommand(produtoId, 4L, new BigDecimal("12.00"), null);

		// Act & Assert
		IllegalStateException ex = assertThrows(IllegalStateException.class, () -> handler.executar(command));
		assertTrue(ex.getMessage().contains("O estoque foi alterado por outra operação"));
		verify(estoqueGateway, never()).salvar(any(), any());
	}

	@Test
	@DisplayName("Deve lançar IllegalArgumentException quando a quantidade contada for igual ao saldo atual")
	void deveLancarQuandoQuantidadeIgual() {
		// Arrange
		Long produtoId = 5L;
		SaldoEstoque saldo = new SaldoEstoque(13L, produtoId, new BigDecimal("4.00"), BigDecimal.ZERO, 1L);
		when(estoqueGateway.buscarSaldoPorProdutoIdParaAlteracao(produtoId)).thenReturn(Optional.of(saldo));

		AjustarInventarioCommand command = new AjustarInventarioCommand(produtoId, 1L, new BigDecimal("4.00"), "Obs");

		// Act & Assert
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> handler.executar(command));
		assertEquals("A quantidade contada é igual ao saldo atual. Nenhum ajuste necessário.", ex.getMessage());
		verify(estoqueGateway, never()).salvar(any(), any());
	}

}