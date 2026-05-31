package com.dinoco.oficina.catalogoproduto.domain;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Produto {

    private Long id;
    private String nome;
    private TipoProduto tipo;
    private String marca;
    private String codigoFabricante;
    private String aplicacao;
    private BigDecimal quantidadeAtual;
    private BigDecimal quantidadeReservada;
    private BigDecimal precoCusto;
    private BigDecimal precoVenda;
    private Boolean ativo;
    private Long versao;

    public Produto(String nome, TipoProduto tipo, String marca, String codigoFabricante, String aplicacao,
                   BigDecimal quantidadeAtual, BigDecimal quantidadeReservada, BigDecimal precoCusto,
                   BigDecimal precoVenda) {
        this.nome = nome;
        this.tipo = tipo;
        this.marca = marca;
        this.codigoFabricante = codigoFabricante;
        this.aplicacao = aplicacao;
        this.quantidadeAtual = quantidadeAtual == null ? BigDecimal.ZERO : quantidadeAtual;
        this.quantidadeReservada = quantidadeReservada == null ? BigDecimal.ZERO : quantidadeReservada;
        this.precoCusto = precoCusto;
        this.precoVenda = precoVenda;
        this.ativo = true;
    }

    public Produto(Long id, Long versao, String nome, TipoProduto tipo, String marca, String codigoFabricante, String aplicacao,
                   BigDecimal quantidadeAtual, BigDecimal quantidadeReservada, BigDecimal precoCusto,
                   BigDecimal precoVenda, Boolean ativo ) {
        this.id = id;
        this.versao = versao;
        this.nome = nome;
        this.tipo = tipo;
        this.marca = marca;
        this.codigoFabricante = codigoFabricante;
        this.aplicacao = aplicacao;
        this.quantidadeAtual = quantidadeAtual == null ? BigDecimal.ZERO : quantidadeAtual;
        this.quantidadeReservada = quantidadeReservada == null ? BigDecimal.ZERO : quantidadeReservada;
        this.precoCusto = precoCusto;
        this.precoVenda = precoVenda;
        this.ativo = ativo;
    }

    public void atualizar(Long versao,
                          String nome,
                          TipoProduto tipo,
                          String marca,
                          String codigoFabricante,
                          String aplicacao,
                          BigDecimal quantidadeAtual,
                          BigDecimal quantidadeReservada,
                          BigDecimal precoCusto,
                          BigDecimal precoVenda) {
        this.versao = versao;
        this.nome = nome;
        this.tipo = tipo;
        this.marca = marca;
        this.codigoFabricante = codigoFabricante;
        this.aplicacao = aplicacao;
        this.quantidadeAtual = quantidadeAtual == null ? BigDecimal.ZERO : quantidadeAtual;
        this.quantidadeReservada = quantidadeReservada == null ? BigDecimal.ZERO : quantidadeReservada;
        this.precoCusto = precoCusto;
        this.precoVenda = precoVenda;
    }

    public void desativar() {
        this.ativo = false;
    }

    // Método de Negócio: Calcula a quantidade virtual em tempo real
    public BigDecimal getQuantidadeDisponivel() {
        return this.quantidadeAtual.subtract(this.quantidadeReservada);
    }

    public boolean isValorVendaInvalido(BigDecimal valorSugerido) {
        if (this.precoCusto == null || valorSugerido == null)
            return false;
        return valorSugerido.compareTo(this.precoCusto) < 0;
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
        this.quantidadeAtual = this.quantidadeAtual.add(quantidade);
    }

    public void consumirQuantidadeReservadaEFisica(BigDecimal quantidade) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("A quantidade para consumo deve ser maior que zero.");
        }
        if (this.quantidadeReservada.compareTo(quantidade) < 0) {
            throw new IllegalStateException("Tentativa de consumir uma quantidade maior do que a reservada.");
        }
        this.quantidadeAtual = this.quantidadeAtual.subtract(quantidade);
        this.quantidadeReservada = this.quantidadeReservada.subtract(quantidade);
    }


}