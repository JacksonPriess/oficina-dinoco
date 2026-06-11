package com.dinoco.oficina.estoque.domain;

import com.dinoco.oficina.estoque.domain.exception.SaldoInsuficienteException;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaldoEstoque {

    private Long id;
    private Long produtoId;
    private BigDecimal quantidadeReal;
    private BigDecimal quantidadeReservada;
    private Long versao;

    // Construtor para QUANDO A PEÇA NASCE NO CATÁLOGO (Prateleira vazia)
    public SaldoEstoque(Long produtoId) {
        this.produtoId = produtoId;
        this.quantidadeReal = BigDecimal.ZERO;
        this.quantidadeReservada = BigDecimal.ZERO;
    }

    // Construtor de RECONSTRUÇÃO (Usado pelo Gateway ao ler do banco)
    public SaldoEstoque(Long id, Long produtoId, BigDecimal quantidadeReal, BigDecimal quantidadeReservada, Long versao) {
        this.id = id;
        this.produtoId = produtoId;
        this.quantidadeReal = quantidadeReal;
        this.quantidadeReservada = quantidadeReservada;
        this.versao = versao;
    }

    public void adicionarEntrada(BigDecimal quantidade) {
        if (quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("A quantidade de entrada deve ser maior que zero.");
        }
        this.quantidadeReal = this.quantidadeReal.add(quantidade);
    }

    public void retirar(BigDecimal quantidade) {
        if (quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("A quantidade de retirada deve ser maior que zero.");
        }
        BigDecimal saldoDisponivel = this.quantidadeReal.subtract(this.quantidadeReservada);

        if (saldoDisponivel.compareTo(quantidade) < 0) {
            throw new SaldoInsuficienteException("Saldo físico disponível insuficiente para esta operação.");
        }
        this.quantidadeReal = this.quantidadeReal.subtract(quantidade);
    }

    // Método de Negócio: Calcula a quantidade virtual em tempo real
    public BigDecimal getQuantidadeDisponivel() {
        return this.getQuantidadeReal().subtract(this.quantidadeReservada);
    }

    public void adicionarQuantidadeReservada(BigDecimal quantidade) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantidade de reserva deve ser maior que zero.");
        }
        this.quantidadeReservada = this.quantidadeReservada.add(quantidade);
    }

    public void atualizarQuantidadeReal(BigDecimal quantidade) {
        if ( quantidade == null ) {
            throw new IllegalArgumentException("Quantidade deve ser informada.");
        }
        this.quantidadeReal = this.quantidadeReal.add(quantidade);
    }

    public void consumirQuantidadeReservadaEFisica(BigDecimal quantidade) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("A quantidade para consumo deve ser maior que zero.");
        }
        if (this.quantidadeReservada.compareTo(quantidade) < 0) {
            throw new IllegalStateException("Tentativa de consumir uma quantidade maior do que a reservada.");
        }
        this.quantidadeReal = this.quantidadeReal.subtract(quantidade);
        this.quantidadeReservada = this.quantidadeReservada.subtract(quantidade);
    }
}
