package com.dinoco.oficina.estoque.application.usecases.commands.registrarentrada;

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
class RegistrarEntradaHandlerTest {

	@InjectMocks
	private RegistrarEntradaHandler handler;

	@Mock
	private EstoqueCommandGateway estoqueGateway;

	@Captor
	private ArgumentCaptor<SaldoEstoque> saldoCaptor;

	@Captor
	private ArgumentCaptor<MovimentacaoEstoque> movCaptor;

	@Test
	@DisplayName("Deve registrar entrada com sucesso, adicionando quantidade ao saldo e criando movimentação")
	void deveRegistrarEntradaComSucesso() {
		// Arrange
		Long produtoId = 1L;
		BigDecimal quantidadeEntrada = new BigDecimal("10.00");
		String observacao = "Compra Fornecedor A";

		SaldoEstoque saldo = new SaldoEstoque(5L, produtoId, new BigDecimal("20.00"), BigDecimal.ZERO, 1L);

		when(estoqueGateway.buscarSaldoPorProdutoIdParaAlteracao(produtoId)).thenReturn(Optional.of(saldo));

		RegistrarEntradaCommand command = new RegistrarEntradaCommand(produtoId, quantidadeEntrada, observacao);

		// Act
		handler.executar(command);

		// Assert
		verify(estoqueGateway, times(1)).salvar(saldoCaptor.capture(), movCaptor.capture());

		SaldoEstoque saldoSalvo = saldoCaptor.getValue();
		MovimentacaoEstoque mov = movCaptor.getValue();

		assertEquals(new BigDecimal("30.00"), saldoSalvo.getQuantidadeReal());
		assertEquals(produtoId, mov.getProdutoId());
		assertEquals(TipoMovimentacao.ENTRADA, mov.getTipo());
		assertEquals(quantidadeEntrada, mov.getQuantidade());
		assertEquals(observacao, mov.getObservacao());
		assertNotNull(mov.getDataMovimentacao());
	}

	@Test
	@DisplayName("Deve registrar entrada em prateleira vazia, iniciando com quantidade da entrada")
	void deveRegistrarEntradaEmPratelheiraVazia() {
		// Arrange
		Long produtoId = 2L;
		BigDecimal quantidadeEntrada = new BigDecimal("5.50");
		String observacao = "Primeira entrada do produto";

		SaldoEstoque saldo = new SaldoEstoque(6L, produtoId, BigDecimal.ZERO, BigDecimal.ZERO, 0L);

		when(estoqueGateway.buscarSaldoPorProdutoIdParaAlteracao(produtoId)).thenReturn(Optional.of(saldo));

		RegistrarEntradaCommand command = new RegistrarEntradaCommand(produtoId, quantidadeEntrada, observacao);

		// Act
		handler.executar(command);

		// Assert
		verify(estoqueGateway, times(1)).salvar(saldoCaptor.capture(), movCaptor.capture());

		SaldoEstoque saldoSalvo = saldoCaptor.getValue();
		MovimentacaoEstoque mov = movCaptor.getValue();

		assertEquals(quantidadeEntrada, saldoSalvo.getQuantidadeReal());
		assertEquals(TipoMovimentacao.ENTRADA, mov.getTipo());
		assertEquals(quantidadeEntrada, mov.getQuantidade());
	}

	@Test
	@DisplayName("Deve lançar IllegalArgumentException quando não encontrar saldo para o produto")
	void deveLancarQuandoSaldoNaoEncontrado() {
		// Arrange
		Long produtoId = 999L;
		when(estoqueGateway.buscarSaldoPorProdutoIdParaAlteracao(produtoId)).thenReturn(Optional.empty());

		RegistrarEntradaCommand command = new RegistrarEntradaCommand(produtoId, new BigDecimal("5.00"), "Obs");

		// Act & Assert
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> handler.executar(command));
		assertEquals("Prateleira não encontrada para este produto.", ex.getMessage());
		verify(estoqueGateway, never()).salvar(any(), any());
	}

	@Test
	@DisplayName("Deve registrar entrada com quantidade fracionada com sucesso")
	void deveRegistrarEntradaComQuantidadeFracionada() {
		// Arrange
		Long produtoId = 3L;
		BigDecimal quantidadeEntrada = new BigDecimal("2.75");
		String observacao = "Entrada de parafuso";

		SaldoEstoque saldo = new SaldoEstoque(7L, produtoId, new BigDecimal("1.50"), BigDecimal.ZERO, 1L);

		when(estoqueGateway.buscarSaldoPorProdutoIdParaAlteracao(produtoId)).thenReturn(Optional.of(saldo));

		RegistrarEntradaCommand command = new RegistrarEntradaCommand(produtoId, quantidadeEntrada, observacao);

		// Act
		handler.executar(command);

		// Assert
		verify(estoqueGateway, times(1)).salvar(saldoCaptor.capture(), movCaptor.capture());

		SaldoEstoque saldoSalvo = saldoCaptor.getValue();
		MovimentacaoEstoque mov = movCaptor.getValue();

		assertEquals(new BigDecimal("4.25"), saldoSalvo.getQuantidadeReal());
		assertEquals(quantidadeEntrada, mov.getQuantidade());
	}

	@Test
	@DisplayName("Deve registrar entrada mantendo quantidade reservada intacta")
	void deveRegistrarEntradaMantendoQuantidadeReservada() {
		// Arrange
		Long produtoId = 4L;
		BigDecimal quantidadeReservada = new BigDecimal("3.00");
		BigDecimal quantidadeEntrada = new BigDecimal("8.00");

		SaldoEstoque saldo = new SaldoEstoque(8L, produtoId, new BigDecimal("10.00"), quantidadeReservada, 2L);

		when(estoqueGateway.buscarSaldoPorProdutoIdParaAlteracao(produtoId)).thenReturn(Optional.of(saldo));

		RegistrarEntradaCommand command = new RegistrarEntradaCommand(produtoId, quantidadeEntrada, "Entrada com reserva");

		// Act
		handler.executar(command);

		// Assert
		verify(estoqueGateway, times(1)).salvar(saldoCaptor.capture(), movCaptor.capture());

		SaldoEstoque saldoSalvo = saldoCaptor.getValue();

		assertEquals(new BigDecimal("18.00"), saldoSalvo.getQuantidadeReal());
		assertEquals(quantidadeReservada, saldoSalvo.getQuantidadeReservada());
	}

	@Test
	@DisplayName("Deve lançar IllegalArgumentException quando quantidade de entrada for zero ou negativa")
	void deveLancarQuandoQuantidadeNegativaOuZero() {
		// Arrange
		Long produtoId = 5L;
		SaldoEstoque saldo = new SaldoEstoque(9L, produtoId, new BigDecimal("5.00"), BigDecimal.ZERO, 1L);

		when(estoqueGateway.buscarSaldoPorProdutoIdParaAlteracao(produtoId)).thenReturn(Optional.of(saldo));

		RegistrarEntradaCommand command = new RegistrarEntradaCommand(produtoId, BigDecimal.ZERO, "Obs");

		// Act & Assert
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> handler.executar(command));
		assertEquals("A quantidade de entrada deve ser maior que zero.", ex.getMessage());
		verify(estoqueGateway, never()).salvar(any(), any());
	}

	@Test
	@DisplayName("Deve registrar entrada com observação vazia com sucesso")
	void deveRegistrarEntradaComObservacaoVazia() {
		// Arrange
		Long produtoId = 6L;
		BigDecimal quantidadeEntrada = new BigDecimal("7.00");

		SaldoEstoque saldo = new SaldoEstoque(10L, produtoId, new BigDecimal("3.00"), BigDecimal.ZERO, 1L);

		when(estoqueGateway.buscarSaldoPorProdutoIdParaAlteracao(produtoId)).thenReturn(Optional.of(saldo));

		RegistrarEntradaCommand command = new RegistrarEntradaCommand(produtoId, quantidadeEntrada, "");

		// Act
		handler.executar(command);

		// Assert
		verify(estoqueGateway, times(1)).salvar(saldoCaptor.capture(), movCaptor.capture());

		MovimentacaoEstoque mov = movCaptor.getValue();

		assertEquals("", mov.getObservacao());
	}

}