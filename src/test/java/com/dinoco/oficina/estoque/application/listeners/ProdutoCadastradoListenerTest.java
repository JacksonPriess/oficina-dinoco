package com.dinoco.oficina.estoque.application.listeners;

import com.dinoco.oficina.estoque.application.gateways.EstoqueCommandGateway;
import com.dinoco.oficina.estoque.domain.MovimentacaoEstoque;
import com.dinoco.oficina.estoque.domain.SaldoEstoque;
import com.dinoco.oficina.estoque.domain.TipoMovimentacao;
import com.dinoco.oficina.estoque.infrastructure.events.ProdutoCadastradoListener;
import com.dinoco.oficina.shared.events.ProdutoCadastradoEvent;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProdutoCadastradoListenerTest {

	@Test
	void quandoProdutoCadastrado_comQuantidade_deveCriarSaldoERegistrarMovimentacao() {



	}

	@Test
	void quandoProdutoCadastrado_semQuantidade_deveCriarSaldoSemMovimentacao() {

	}

}