package com.dinoco.oficina.ordemservico.application.saga;

import com.dinoco.oficina.ordemservico.application.gateways.ComandoFinanceiroGateway;
import com.dinoco.oficina.ordemservico.application.gateways.SagaPagamentoGateway;
import com.dinoco.oficina.ordemservico.domain.exceptions.RegraNegocioOSException;
import com.dinoco.oficina.ordemservico.domain.models.SagaPagamento;
import com.dinoco.oficina.ordemservico.domain.models.SagaPagamento.Estado;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class OrquestradorPagamentoTest {

    private final SagaPagamentoGateway sagas = mock(SagaPagamentoGateway.class);
    private final ComandoFinanceiroGateway comandos = mock(ComandoFinanceiroGateway.class);
    private final OrquestradorPagamento orquestrador = new OrquestradorPagamento(sagas, comandos);
    private final SagaPagamento inicial = SagaPagamento.iniciar(1L, new BigDecimal("250.50"));
    private final UUID pagamentoId = UUID.randomUUID();

    @Test
    void registraSagaEComandoUmaVezAoFinalizar() {
        orquestrador.iniciar(1L, inicial.valor());
        var captor = ArgumentCaptor.forClass(SagaPagamento.class);
        verify(sagas).salvar(captor.capture());
        var criada = captor.getValue();
        assertThat(criada.estado()).isEqualTo(Estado.SOLICITADA);
        verify(comandos).criarPagamento(criada);

        when(sagas.buscarPorOsId(1L)).thenReturn(Optional.of(criada));
        orquestrador.iniciar(1L, inicial.valor());
        verifyNoMoreInteractions(comandos);
    }

    @Test
    void cobraEmCentavosMesmoComQuantidadeFracionadaNaOs() {
        orquestrador.iniciar(1L, new BigDecimal("250.505"));
        var captor = ArgumentCaptor.forClass(SagaPagamento.class);
        verify(comandos).criarPagamento(captor.capture());
        var saga = captor.getValue();
        assertThat(saga.valor()).isEqualTo(new BigDecimal("250.51"));
        estadoAtual(saga.receberResultado("pagamento.aprovado.v1", pagamentoId, "provedor-1", null));
        orquestrador.autorizarEntrega(1L, new BigDecimal("250.505"));
    }

    @Test
    void osSemValorNaoCriaCobranca() {
        orquestrador.iniciar(1L, BigDecimal.ZERO);
        orquestrador.autorizarEntrega(1L, BigDecimal.ZERO);
        verifyNoInteractions(sagas, comandos);
    }

    @Test
    void bloqueiaEntregaPendenteEAceitaAprovacaoAntesDeEventoCriado() {
        estadoAtual(inicial);
        assertThatThrownBy(() -> orquestrador.autorizarEntrega(1L, inicial.valor()))
                .isInstanceOf(RegraNegocioOSException.class);

        var aprovada = orquestrador.receber(evento("pagamento.aprovado.v1", null));
        estadoAtual(aprovada);
        assertThat(orquestrador.receber(evento("pagamento.criado.v1", null))).isEqualTo(aprovada);
        assertThat(orquestrador.receber(evento("pagamento.aprovado.v1", null))).isEqualTo(aprovada);

        orquestrador.autorizarEntrega(1L, inicial.valor());
        verify(sagas).salvar(aprovada.registrarEntrega());
    }

    @Test
    void solicitacaoDeEstornoBloqueiaEntregaEAprovacaoAtrasada() {
        var aprovada = inicial.receberResultado("pagamento.aprovado.v1", pagamentoId, "provedor-1", null);
        estadoAtual(aprovada);
        orquestrador.solicitarEstorno(1L);
        var emEstorno = aprovada.solicitarEstorno();
        verify(comandos).estornarPagamento(emEstorno);

        estadoAtual(emEstorno);
        orquestrador.solicitarEstorno(1L);
        assertThat(orquestrador.receber(evento("pagamento.aprovado.v1", null))).isEqualTo(emEstorno);
        assertThatThrownBy(() -> orquestrador.autorizarEntrega(1L, inicial.valor()))
                .isInstanceOf(RegraNegocioOSException.class);

        var estornada = orquestrador.receber(evento("pagamento.estornado.v1", "estorno-1"));
        assertThat(estornada.estado()).isEqualTo(Estado.ESTORNADA);
        assertThat(estornada.estornoId()).isEqualTo("estorno-1");
        estadoAtual(estornada);
        assertThat(orquestrador.receber(evento("pagamento.estornado.v1", "estorno-1"))).isEqualTo(estornada);
        verifyNoMoreInteractions(comandos);
    }

    @Test
    void rejeitaEstornoDeOsEntregue() {
        estadoAtual(inicial.receberResultado("pagamento.aprovado.v1", pagamentoId, "provedor-1", null).registrarEntrega());
        assertThatThrownBy(() -> orquestrador.solicitarEstorno(1L)).isInstanceOf(RegraNegocioOSException.class);
        verifyNoInteractions(comandos);
    }

    @Test
    void rejeitaEventoComValorDivergente() {
        estadoAtual(inicial);
        var invalido = new EventoFinanceiro(UUID.randomUUID(), "pagamento.aprovado.v1", inicial.sagaId(),
                1L, pagamentoId, BigDecimal.ONE, "BRL", "provedor-1", null, null);
        assertThatThrownBy(() -> orquestrador.receber(invalido)).isInstanceOf(IllegalArgumentException.class);
        verify(sagas, never()).salvar(any());
    }

    private EventoFinanceiro evento(String tipo, String estornoId) {
        return new EventoFinanceiro(UUID.randomUUID(), tipo, inicial.sagaId(), 1L, pagamentoId,
                inicial.valor(), "BRL", "provedor-1", estornoId, null);
    }

    private void estadoAtual(SagaPagamento saga) {
        when(sagas.bloquearPorOsId(1L)).thenReturn(Optional.of(saga));
    }
}
