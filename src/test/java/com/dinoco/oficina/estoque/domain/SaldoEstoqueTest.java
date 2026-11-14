package com.dinoco.oficina.estoque.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class SaldoEstoqueTest {

    @Test
    void deveCriarSaldoEstoqueComSucesso() {
        // Arrange
        var saldoId = 1L;
        var produtoId = 1L;
        var quantidade = 10;

        // Act
        var saldo = new SaldoEstoque(saldoId, produtoId, new BigDecimal(quantidade), new BigDecimal(0), 0L);

        // Assert
        assertNotNull(saldo);
        assertEquals(produtoId, saldo.getProdutoId());
        assertEquals(BigDecimal.valueOf(quantidade), saldo.getQuantidadeReal());
    }

    @Test
    void deveAdicionarEntradaAumentandoQuantidadeReal() {
        // Arrange
        var produtoId = 2L;
        var saldo = new SaldoEstoque(produtoId);

        // Act
        saldo.adicionarEntrada(BigDecimal.valueOf(5));

        // Assert
        assertEquals(BigDecimal.valueOf(5), saldo.getQuantidadeReal());
    }

    @Test
    void deveRetirarQuantidadeQuandoSuficiente() {
        // Arrange: 10 reais, 2 reservados -> disponível 8
        var saldo = new SaldoEstoque(1L, 2L, BigDecimal.valueOf(10), BigDecimal.valueOf(2), 0L);

        // Act
        saldo.retirar(BigDecimal.valueOf(5));

        // Assert: quantidadeReal reduzida em 5, reservado permanece
        assertEquals(BigDecimal.valueOf(5), saldo.getQuantidadeReal());
        assertEquals(BigDecimal.valueOf(2), saldo.getQuantidadeReservada());
    }

    @Test
    void deveAdicionarQuantidadeReservada() {
        // Arrange
        var saldo = new SaldoEstoque(1L, 3L, BigDecimal.valueOf(10), BigDecimal.ZERO, 0L);

        // Act
        saldo.adicionarQuantidadeReservada(BigDecimal.valueOf(4));

        // Assert
        assertEquals(BigDecimal.valueOf(4), saldo.getQuantidadeReservada());
    }

    @Test
    void deveConsumirQuantidadeReservadaEFisica() {
        // Arrange: real 10, reservado 5
        var saldo = new SaldoEstoque(1L, 4L, BigDecimal.valueOf(10), BigDecimal.valueOf(5), 0L);

        // Act
        saldo.consumirQuantidadeReservadaEFisica(BigDecimal.valueOf(3));

        // Assert: real e reservado reduzidos em 3
        assertEquals(BigDecimal.valueOf(7), saldo.getQuantidadeReal());
        assertEquals(BigDecimal.valueOf(2), saldo.getQuantidadeReservada());
    }

    @Test
    void deveTerVersaoInicialZeroAoCriarSaldo() {
        // Arrange & Act
        var saldo = new SaldoEstoque(99L);
        // Assert
        assertEquals(Long.valueOf(0), saldo.getVersao());
    }


    @Test
    void deveRejeitarEntradaERetiradaNaoPositivasSemAlterarSaldo() {
        for (var quantidade : new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ONE.negate()}) {
            var saldo = new SaldoEstoque(1L, 2L, BigDecimal.TEN, BigDecimal.ONE, 0L);
            assertThrows(IllegalArgumentException.class, () -> saldo.adicionarEntrada(quantidade));
            assertThrows(IllegalArgumentException.class, () -> saldo.retirar(quantidade));
            assertEquals(BigDecimal.TEN, saldo.getQuantidadeReal());
            assertEquals(BigDecimal.ONE, saldo.getQuantidadeReservada());
        }
    }

    @Test
    void deveProtegerQuantidadeReservadaNaRetirada() {
        var saldo = new SaldoEstoque(1L, 2L, BigDecimal.TEN, new BigDecimal("8"), 0L);

        assertThrows(com.dinoco.oficina.estoque.domain.exception.SaldoInsuficienteException.class,
                () -> saldo.retirar(new BigDecimal("3")));

        assertEquals(BigDecimal.TEN, saldo.getQuantidadeReal());
        assertEquals(new BigDecimal("8"), saldo.getQuantidadeReservada());
    }

    @Test
    void devePermitirRetiradaExataDoDisponivel() {
        var saldo = new SaldoEstoque(1L, 2L, new BigDecimal("10.5"), new BigDecimal("8.0"), 0L);

        saldo.retirar(new BigDecimal("2.5"));

        assertEquals(new BigDecimal("8.0"), saldo.getQuantidadeReal());
        assertEquals(new BigDecimal("8.0"), saldo.getQuantidadeReservada());
    }

    @Test
    void deveRejeitarReservaEConsumoInvalidosSemAlterarSaldo() {
        for (var quantidade : new BigDecimal[]{null, BigDecimal.ZERO, BigDecimal.ONE.negate()}) {
            var saldo = new SaldoEstoque(1L, 2L, BigDecimal.TEN, BigDecimal.ONE, 0L);
            assertThrows(IllegalArgumentException.class, () -> saldo.adicionarQuantidadeReservada(quantidade));
            assertThrows(IllegalArgumentException.class, () -> saldo.consumirQuantidadeReservadaEFisica(quantidade));
            assertEquals(BigDecimal.TEN, saldo.getQuantidadeReal());
            assertEquals(BigDecimal.ONE, saldo.getQuantidadeReservada());
        }
    }

    @Test
    void deveRejeitarConsumoSuperiorAReservaSemAlterarSaldo() {
        var saldo = new SaldoEstoque(1L, 2L, BigDecimal.TEN, BigDecimal.ONE, 0L);

        assertThrows(IllegalStateException.class, () -> saldo.consumirQuantidadeReservadaEFisica(new BigDecimal("2")));

        assertEquals(BigDecimal.TEN, saldo.getQuantidadeReal());
        assertEquals(BigDecimal.ONE, saldo.getQuantidadeReservada());
    }

    @Test
    void deveConsumirReservaFracionadaIntegralmente() {
        var saldo = new SaldoEstoque(1L, 2L, new BigDecimal("2.50"), new BigDecimal("2.50"), 0L);

        saldo.consumirQuantidadeReservadaEFisica(new BigDecimal("2.50"));

        assertEquals(0, saldo.getQuantidadeReal().signum());
        assertEquals(0, saldo.getQuantidadeReservada().signum());
    }

    @Test
    void deveAcumularReservaSemBaixarEstoqueFisico() {
        var saldo = new SaldoEstoque(1L, 2L, BigDecimal.TEN, BigDecimal.ONE, 0L);

        saldo.adicionarQuantidadeReservada(new BigDecimal("1.25"));

        assertEquals(new BigDecimal("2.25"), saldo.getQuantidadeReservada());
        assertEquals(BigDecimal.TEN, saldo.getQuantidadeReal());
    }
}