package com.dinoco.oficina.ordemservico.domain.models;

import com.dinoco.oficina.ordemservico.domain.exceptions.RegraNegocioOSException;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class SagaPagamentoTest {

    private final UUID pagamentoId = UUID.fromString("00000000-0000-4000-8000-000000000001");

    @Test
    void devePreservarIdentidadeEValorAoAvancarAteEntrega() {
        var inicial = SagaPagamento.iniciar(42L, new BigDecimal("250.50"));
        var pendente = inicial.receberResultado("pagamento.criado.v1", pagamentoId, "p-1", null);
        var aprovada = pendente.receberResultado("pagamento.aprovado.v1", pagamentoId, "p-1", null);
        var entregue = aprovada.registrarEntrega();
        assertEquals(SagaPagamento.Estado.SOLICITADA, inicial.estado());
        assertEquals(SagaPagamento.Estado.PENDENTE, pendente.estado());
        assertEquals(SagaPagamento.Estado.APROVADA, aprovada.estado());
        assertEquals(SagaPagamento.Estado.ENTREGUE, entregue.estado());
        assertEquals(inicial.sagaId(), entregue.sagaId());
        assertEquals(inicial.osId(), entregue.osId());
        assertEquals(inicial.valor(), entregue.valor());
        assertEquals(pagamentoId, entregue.pagamentoId());
        assertEquals("p-1", entregue.provedorId());
    }

    @Test
    void deveRejeitarIdentificadoresAusentesOuDivergentes() {
        var inicial = SagaPagamento.iniciar(1L, BigDecimal.TEN);
        assertThrows(IllegalArgumentException.class, () -> inicial.receberResultado("pagamento.criado.v1", null, "p-1", null));
        for (var provedor : new String[]{null, "", " "}) {
            assertThrows(IllegalArgumentException.class, () -> inicial.receberResultado("pagamento.criado.v1", pagamentoId, provedor, null));
        }
        var aprovada = aprovada();
        assertThrows(IllegalArgumentException.class, () -> aprovada.receberResultado("pagamento.aprovado.v1", UUID.randomUUID(), "p-1", null));
        assertThrows(IllegalArgumentException.class, () -> aprovada.receberResultado("pagamento.aprovado.v1", pagamentoId, "outro", null));
    }

    @Test
    void deveIgnorarCriacaoAtrasadaEAprovacaoRepetida() {
        var aprovada = aprovada();
        assertSame(aprovada, aprovada.receberResultado("pagamento.criado.v1", pagamentoId, "p-1", null));
        assertSame(aprovada, aprovada.receberResultado("pagamento.aprovado.v1", pagamentoId, "p-1", null));
    }

    @Test
    void deveConfirmarEstornoUmaVezESemRegredirPorAprovacaoAtrasada() {
        var solicitada = aprovada().solicitarEstorno();
        assertSame(solicitada, solicitada.solicitarEstorno());
        assertSame(solicitada, solicitada.receberResultado("pagamento.aprovado.v1", pagamentoId, "p-1", null));
        var estornada = solicitada.receberResultado("pagamento.estornado.v1", pagamentoId, "p-1", "e-1");
        assertEquals(SagaPagamento.Estado.ESTORNADA, estornada.estado());
        assertEquals("e-1", estornada.estornoId());
        assertSame(estornada, estornada.receberResultado("pagamento.estornado.v1", pagamentoId, "p-1", "e-1"));
        assertSame(estornada, estornada.solicitarEstorno());
        assertThrows(IllegalStateException.class, () -> estornada.receberResultado("pagamento.estornado.v1", pagamentoId, "p-1", "e-2"));
    }

    @Test
    void deveRejeitarEstornoSemSolicitacaoOuSemIdentificador() {
        var aprovada = aprovada();
        assertThrows(IllegalStateException.class, () -> aprovada.receberResultado("pagamento.estornado.v1", pagamentoId, "p-1", "e-1"));
        var solicitada = aprovada.solicitarEstorno();
        for (var estorno : new String[]{null, "", " "}) {
            assertThrows(IllegalArgumentException.class, () -> solicitada.receberResultado("pagamento.estornado.v1", pagamentoId, "p-1", estorno));
        }
    }

    @Test
    void deveBloquearEntregaEmTodosEstadosSemAprovacaoDisponivel() {
        for (var estado : SagaPagamento.Estado.values()) {
            if (estado == SagaPagamento.Estado.APROVADA) continue;
            var saga = new SagaPagamento(UUID.randomUUID(), 1L, BigDecimal.TEN, estado, pagamentoId, "p-1", null);
            assertThrows(RegraNegocioOSException.class, saga::registrarEntrega, estado.name());
        }
    }

    @Test
    void deveRejeitarSolicitacaoDeEstornoAntesDeAprovarOuAposEntrega() {
        for (var estado : new SagaPagamento.Estado[]{SagaPagamento.Estado.SOLICITADA, SagaPagamento.Estado.PENDENTE, SagaPagamento.Estado.ENTREGUE}) {
            var saga = new SagaPagamento(UUID.randomUUID(), 1L, BigDecimal.TEN, estado, pagamentoId, "p-1", null);
            assertThrows(RegraNegocioOSException.class, saga::solicitarEstorno, estado.name());
        }
    }

    @Test
    void deveRejeitarEventoDesconhecido() {
        assertThrows(IllegalArgumentException.class, () -> aprovada().receberResultado("desconhecido", pagamentoId, "p-1", null));
    }

    private SagaPagamento aprovada() {
        return SagaPagamento.iniciar(1L, BigDecimal.TEN)
                .receberResultado("pagamento.aprovado.v1", pagamentoId, "p-1", null);
    }
}
