package com.dinoco.oficina.estoque.application.listeners;

import com.dinoco.oficina.estoque.application.gateways.EstoqueCommandGateway;
import com.dinoco.oficina.estoque.domain.MovimentacaoEstoque;
import com.dinoco.oficina.estoque.domain.SaldoEstoque;
import com.dinoco.oficina.estoque.domain.TipoMovimentacao;
import com.dinoco.oficina.shared.events.ProdutoCadastradoEvent;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProdutoCadastradoListenerTest {

	@Test
	void quandoProdutoCadastrado_comQuantidade_deveCriarSaldoERegistrarMovimentacao() {
		// Arrange
		var gateway = mock(EstoqueCommandGateway.class);
		var listener = new ProdutoCadastradoListener(gateway);

		class Holder { SaldoEstoque saldo; MovimentacaoEstoque mov; }
		var h = new Holder();

		doAnswer(invocation -> {
			h.saldo = invocation.getArgument(0);
			h.mov = invocation.getArgument(1);
			return null;
		}).when(gateway).salvar(any(), any());

		var produtoId = 10L;
		var quantidade = BigDecimal.valueOf(7);

		// Act
		listener.criarPrateleiraAoCadastrarProduto(new ProdutoCadastradoEvent(produtoId, quantidade));

		// Assert
		assertNotNull(h.saldo);
		assertEquals(produtoId, h.saldo.getProdutoId());
		assertEquals(quantidade, h.saldo.getQuantidadeReal());

		assertNotNull(h.mov);
		assertEquals(TipoMovimentacao.ENTRADA, h.mov.getTipo());
		assertEquals(quantidade, h.mov.getQuantidade());
	}

	@Test
	void quandoProdutoCadastrado_semQuantidade_deveCriarSaldoSemMovimentacao() {
		// Arrange
		var gateway = mock(EstoqueCommandGateway.class);
		var listener = new ProdutoCadastradoListener(gateway);

		class Holder { SaldoEstoque saldo; MovimentacaoEstoque mov; }
		var h = new Holder();

		doAnswer(invocation -> {
			h.saldo = invocation.getArgument(0);
			h.mov = invocation.getArgument(1);
			return null;
		}).when(gateway).salvar(any(), any());

		var produtoId = 11L;

		// Act: quantidade nula
		listener.criarPrateleiraAoCadastrarProduto(new ProdutoCadastradoEvent(produtoId, null));

		// Assert
		assertNotNull(h.saldo);
		assertEquals(produtoId, h.saldo.getProdutoId());
		assertEquals(BigDecimal.ZERO, h.saldo.getQuantidadeReal());

		assertNull(h.mov);
	}

}