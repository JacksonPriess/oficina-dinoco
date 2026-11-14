package com.dinoco.oficina.ordemservico.domain.models;

import com.dinoco.oficina.ordemservico.domain.enums.StatusItemServico;
import com.dinoco.oficina.ordemservico.domain.exceptions.RegraNegocioOSException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ItemOSServicoTest {

    @Test
    void deveRejeitarPrecoNegativoNaCriacaoENaAlteracao() {
        assertThrows(IllegalArgumentException.class, () -> new ItemOSServico(1L, null, BigDecimal.ONE.negate()));
        var item = novo();
        assertThrows(IllegalArgumentException.class, () -> item.alterarDados(BigDecimal.ONE.negate(), 2L));
        assertEquals(BigDecimal.TEN, item.getValorCobrado());
        assertEquals(1L, item.getMecanicoId());
    }

    @Test
    void deveAtualizarSomenteCamposInformados() {
        var item = novo();
        item.alterarDados(null, 2L);
        assertEquals(BigDecimal.TEN, item.getValorCobrado());
        assertEquals(2L, item.getMecanicoId());
        item.alterarDados(BigDecimal.ZERO, null);
        assertEquals(BigDecimal.ZERO, item.getValorCobrado());
        assertEquals(2L, item.getMecanicoId());
    }

    @Test
    void deveRejeitarReinicioDeServicoEmAndamento() {
        var item = novo();
        item.iniciarExecucao();
        var inicio = item.getDataInicio();
        assertThrows(IllegalStateException.class, item::iniciarExecucao);
        assertEquals(inicio, item.getDataInicio());
        assertEquals(StatusItemServico.EM_ANDAMENTO, item.getStatusItem());
    }

    @Test
    void deveRejeitarConclusaoAntesDeIniciar() {
        var item = novo();
        assertThrows(IllegalStateException.class, () -> item.concluirExecucao(null));
        assertEquals(StatusItemServico.PENDENTE, item.getStatusItem());
        assertNull(item.getDataFim());
    }

    @Test
    void deveRejeitarFimAnteriorAoInicioSemConcluir() {
        var item = novo();
        item.iniciarExecucao();
        assertThrows(IllegalArgumentException.class, () -> item.concluirExecucao(item.getDataInicio().minusSeconds(1)));
        assertEquals(StatusItemServico.EM_ANDAMENTO, item.getStatusItem());
        assertNull(item.getDataFim());
    }

    @Test
    void deveAceitarConclusaoNoMesmoInstanteDoInicio() {
        var item = novo();
        item.iniciarExecucao();
        item.concluirExecucao(item.getDataInicio());
        assertEquals(StatusItemServico.CONCLUIDO, item.getStatusItem());
        assertEquals(item.getDataInicio(), item.getDataFim());
    }

    @Test
    void deveRegistrarHorarioAtualQuandoFimNaoInformado() {
        var item = novo();
        item.iniciarExecucao();
        var antes = LocalDateTime.now();
        item.concluirExecucao(null);
        var depois = LocalDateTime.now();
        assertFalse(item.getDataFim().isBefore(antes));
        assertFalse(item.getDataFim().isAfter(depois));
    }

    @Test
    void deveImpedirAlteracaoReinicioEConclusaoRepetidaDeServicoConcluido() {
        var item = novo();
        item.iniciarExecucao();
        item.concluirExecucao(item.getDataInicio());
        var fim = item.getDataFim();
        assertThrows(RegraNegocioOSException.class, () -> item.alterarDados(BigDecimal.ONE, 2L));
        assertThrows(IllegalStateException.class, () -> item.atribuirMecanico(2L));
        assertThrows(IllegalStateException.class, item::iniciarExecucao);
        assertThrows(IllegalStateException.class, () -> item.concluirExecucao(null));
        assertEquals(BigDecimal.TEN, item.getValorCobrado());
        assertEquals(1L, item.getMecanicoId());
        assertEquals(fim, item.getDataFim());
        assertEquals(StatusItemServico.CONCLUIDO, item.getStatusItem());
    }

    private ItemOSServico novo() {
        return new ItemOSServico(1L, 1L, BigDecimal.TEN);
    }
}
