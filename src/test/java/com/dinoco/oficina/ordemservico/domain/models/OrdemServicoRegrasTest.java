package com.dinoco.oficina.ordemservico.domain.models;

import com.dinoco.oficina.ordemservico.domain.enums.StatusOS;
import com.dinoco.oficina.ordemservico.domain.exceptions.RegraNegocioOSException;
import com.dinoco.oficina.ordemservico.domain.exceptions.TransicaoStatusInvalidaException;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class OrdemServicoRegrasTest {

    @Test
    void deveBloquearAdicaoDeItensAposOrcamento() {
        var permitidos = Set.of(StatusOS.RECEBIDA, StatusOS.EM_DIAGNOSTICO, StatusOS.AGUARDANDO_ORCAMENTO);
        for (var status : StatusOS.values()) {
            if (permitidos.contains(status)) continue;
            var os = nova(status);
            assertThrows(RegraNegocioOSException.class,
                    () -> os.adicionarProduto(new ItemOSProduto(1L, BigDecimal.ONE, BigDecimal.TEN)), status.name());
            assertThrows(RegraNegocioOSException.class,
                    () -> os.adicionarServico(new ItemOSServico(1L, 1L, BigDecimal.TEN)), status.name());
            assertTrue(os.getItensProduto().isEmpty());
            assertTrue(os.getItensServico().isEmpty());
            assertEquals(status, os.getStatus());
        }
    }

    @Test
    void deveBloquearAlteracaoDeItensForaDoDiagnosticoEOrcamento() {
        for (var status : StatusOS.values()) {
            if (status == StatusOS.EM_DIAGNOSTICO || status == StatusOS.AGUARDANDO_ORCAMENTO) continue;
            var os = nova(StatusOS.RECEBIDA);
            var produto = new ItemOSProduto(1L, 1L, BigDecimal.ONE, BigDecimal.TEN);
            var servico = new ItemOSServico(1L, 1L, BigDecimal.TEN);
            servico.setId(1L);
            os.adicionarProduto(produto);
            os.adicionarServico(servico);
            os.setStatus(status);
            assertThrows(RegraNegocioOSException.class, () -> os.alterarItemProduto(1L, BigDecimal.ONE, null), status.name());
            assertThrows(RegraNegocioOSException.class, () -> os.alterarItemServico(1L, BigDecimal.ONE, null), status.name());
            assertEquals(BigDecimal.TEN, produto.getValorUnitarioVenda());
            assertEquals(BigDecimal.TEN, servico.getValorCobrado());
        }
    }

    @Test
    void deveRejeitarExecucaoDeItemForaDaExecucaoDaOs() {
        for (var status : StatusOS.values()) {
            if (status == StatusOS.EM_EXECUCAO) continue;
            var os = nova(status);
            assertThrows(RegraNegocioOSException.class, () -> os.iniciarExecucaoItemServico(1L), status.name());
            assertThrows(RegraNegocioOSException.class, () -> os.concluirExecucaoItemServico(1L, null), status.name());
        }
    }

    @Test
    void deveRejeitarItemInexistenteDuranteExecucao() {
        var os = nova(StatusOS.EM_EXECUCAO);
        assertThrows(RegraNegocioOSException.class, () -> os.iniciarExecucaoItemServico(99L));
        assertThrows(RegraNegocioOSException.class, () -> os.concluirExecucaoItemServico(99L, null));
    }

    @Test
    void deveRejeitarTransicoesInvalidasSemMudarStatusOuDatas() {
        for (var status : StatusOS.values()) {
            var os = nova(status);
            if (status != StatusOS.AGUARDANDO_APROVACAO) {
                assertThrows(TransicaoStatusInvalidaException.class, os::reprovarOrcamento, status.name());
                assertThrows(TransicaoStatusInvalidaException.class, os::marcarAguardandoFornecedor, status.name());
            }
            if (status != StatusOS.AGUARDANDO_APROVACAO && status != StatusOS.AGUARDANDO_FORNECEDOR)
                assertThrows(TransicaoStatusInvalidaException.class, os::marcarProntaParaExecucao, status.name());
            if (status != StatusOS.AGUARDANDO_EXECUCAO)
                assertThrows(TransicaoStatusInvalidaException.class, os::iniciarExecucao, status.name());
            if (status != StatusOS.EM_EXECUCAO)
                assertThrows(TransicaoStatusInvalidaException.class, os::finalizarExecucao, status.name());
            if (status != StatusOS.FINALIZADA)
                assertThrows(TransicaoStatusInvalidaException.class, os::concluir, status.name());
            assertEquals(status, os.getStatus());
            assertNull(os.getDataReprovacao());
            assertNull(os.getDataInicioExecucao());
            assertNull(os.getDataFinalExecucao());
            assertNull(os.getDataSaida());
        }
    }

    @Test
    void deveRecalcularTotalComQuantidadeFracionadaAposAlteracao() {
        var os = nova(StatusOS.EM_DIAGNOSTICO);
        os.adicionarProduto(new ItemOSProduto(1L, 2L, BigDecimal.ONE, BigDecimal.TEN));
        os.adicionarServico(new ItemOSServico(3L, 1L, new BigDecimal("100")));
        os.setValorDesconto(new BigDecimal("5"));

        os.alterarItemProduto(1L, new BigDecimal("12.50"), new BigDecimal("2.5"));

        assertEquals(0, new BigDecimal("31.25").compareTo(os.getValorTotalProdutos()));
        assertEquals(0, new BigDecimal("126.25").compareTo(os.getValorTotalOS()));
    }

    private OrdemServico nova(StatusOS status) {
        var os = new OrdemServico(1L, 1L, 50000, "Revisão");
        os.setStatus(status);
        return os;
    }
}
