package com.dinoco.oficina.ordemservico.domain.models;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ItemOSProdutoTest {

    @Test
    void deveRejeitarQuantidadeNaoPositivaNoCadastro() {
        for (var quantidade : new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ONE.negate()}) {
            assertThrows(IllegalArgumentException.class, () -> new ItemOSProduto(1L, quantidade, BigDecimal.TEN));
        }
    }

    @Test
    void deveRejeitarNovaQuantidadeNaoPositiva() {
        for (var quantidade : new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ONE.negate()}) {
            var item = new ItemOSProduto(1L, BigDecimal.ONE, BigDecimal.TEN);
            assertThrows(IllegalArgumentException.class, () -> item.alterarDados(null, quantidade));
            assertEquals(BigDecimal.ONE, item.getQuantidade());
            assertEquals(BigDecimal.TEN, item.getValorUnitarioVenda());
        }
    }

    @Test
    void deveCalcularTotalFracionadoEAtualizarPrecoEQuantidade() {
        var item = new ItemOSProduto(1L, new BigDecimal("1.5"), new BigDecimal("10.20"));
        assertEquals(0, new BigDecimal("15.30").compareTo(item.getValorTotal()));

        item.alterarDados(new BigDecimal("12.50"), new BigDecimal("2.5"));

        assertEquals(0, new BigDecimal("31.25").compareTo(item.getValorTotal()));
    }

    @Test
    void devePreservarCamposNaoInformadosNaAlteracao() {
        var item = new ItemOSProduto(1L, BigDecimal.ONE, BigDecimal.TEN);
        item.alterarDados(null, null);
        assertEquals(BigDecimal.ONE, item.getQuantidade());
        assertEquals(BigDecimal.TEN, item.getValorUnitarioVenda());
    }

    @Test
    void deveRejeitarPrecoNegativoSemAlterarItem() {
        var item = new ItemOSProduto(1L, BigDecimal.ONE, BigDecimal.TEN);
        assertThrows(IllegalArgumentException.class, () -> item.alterarDados(BigDecimal.ONE.negate(), null));
        assertEquals(BigDecimal.TEN, item.getValorUnitarioVenda());
    }

    @Test
    void deveAceitarPrecoZeroEnquantoAguardaCotacao() {
        var item = new ItemOSProduto(1L, BigDecimal.ONE, BigDecimal.ZERO);
        assertEquals(0, item.getValorTotal().signum());
    }
}
