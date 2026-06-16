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

}