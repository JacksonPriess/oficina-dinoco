package com.dinoco.oficina.estoque.application.usecases.commands.inicializar;

import com.dinoco.oficina.estoque.application.gateways.*;
import com.dinoco.oficina.estoque.domain.*;
import com.dinoco.oficina.shared.events.ProdutoCadastradoEvent;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InicializarEstoqueHandlerTest {

    final EstoqueCommandGateway estoque = mock(EstoqueCommandGateway.class);
    final InicializacaoEstoqueGateway inbox = mock(InicializacaoEstoqueGateway.class);
    final InicializarEstoqueHandler handler = new InicializarEstoqueHandler(estoque, inbox);

    @Test
    void entradaInicial() {
        var evento = new ProdutoCadastradoEvent(UUID.randomUUID(), 123L, new BigDecimal("10"));
        when(inbox.registrarSeNovo(evento)).thenReturn(true);
        when(estoque.buscarSaldoPorProdutoIdParaAlteracao(123L)).thenReturn(Optional.empty());
        handler.executar(evento);
        var saldo = ArgumentCaptor.forClass(SaldoEstoque.class);
        var mov = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
        verify(estoque).salvar(saldo.capture(), mov.capture());
        assertEquals(0, new BigDecimal("10").compareTo(saldo.getValue().getQuantidadeReal()));
        assertEquals(TipoMovimentacao.ENTRADA, mov.getValue().getTipo());
        assertEquals(123L, mov.getValue().getProdutoId());
    }

    @Test
    void quantidadeAusenteCriaPrateleiraSemMovimentacao() {
        var evento = new ProdutoCadastradoEvent(UUID.randomUUID(), 123L, null);
        when(inbox.registrarSeNovo(evento)).thenReturn(true);
        when(estoque.buscarSaldoPorProdutoIdParaAlteracao(123L)).thenReturn(Optional.empty());
        handler.executar(evento);
        var saldo = ArgumentCaptor.forClass(SaldoEstoque.class);
        verify(estoque).salvar(saldo.capture(), isNull());
        assertEquals(0, saldo.getValue().getQuantidadeReal().signum());
    }

    @Test
    void reentregaNaoRepeteEntrada() {
        var evento = new ProdutoCadastradoEvent(UUID.randomUUID(), 123L, BigDecimal.TEN);
        when(inbox.registrarSeNovo(evento)).thenReturn(false);
        handler.executar(evento);
        verifyNoInteractions(estoque);
    }

    @Test
    void saldoPreexistenteNaoPodeSerSobrescrito() {
        var evento = new ProdutoCadastradoEvent(UUID.randomUUID(), 123L, BigDecimal.TEN);
        when(inbox.registrarSeNovo(evento)).thenReturn(true);
        when(estoque.buscarSaldoPorProdutoIdParaAlteracao(123L)).thenReturn(Optional.of(new SaldoEstoque(123L)));
        assertThrows(IllegalStateException.class, () -> handler.executar(evento));
        verify(estoque, never()).salvar(any(), any());
    }

    @Test
    void falhaDeGravacaoPropagaParaRollbackERetry() {
        var evento = new ProdutoCadastradoEvent(UUID.randomUUID(), 123L, BigDecimal.TEN);
        when(inbox.registrarSeNovo(evento)).thenReturn(true);
        when(estoque.buscarSaldoPorProdutoIdParaAlteracao(123L)).thenReturn(Optional.empty());
        doThrow(new IllegalStateException("Banco indisponível")).when(estoque).salvar(any(), any());
        assertThrows(IllegalStateException.class, () -> handler.executar(evento));
    }
}