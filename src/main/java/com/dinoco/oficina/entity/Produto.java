package com.dinoco.oficina.entity;

import com.dinoco.oficina.catalogoproduto.domain.TipoProduto;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "produto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoProduto tipo;

    @Column(length = 100)
    private String marca;

    @Column(name = "codigo_fabricante", length = 100)
    private String codigoFabricante;

    @Column(columnDefinition = "TEXT")
    private String aplicacao;

    @Column(name = "quantidade_atual", nullable = false, columnDefinition = "DECIMAL(10,3) DEFAULT 0.000")
    private BigDecimal quantidadeAtual = BigDecimal.ZERO;

    @Column(name = "quantidade_reservada", nullable = false, columnDefinition = "DECIMAL(10,3) DEFAULT 0.000")
    private BigDecimal quantidadeReservada = BigDecimal.ZERO;

    @Column(name = "preco_custo", nullable = false)
    private BigDecimal precoCusto = BigDecimal.ZERO;

    @Column(name = "preco_venda", nullable = false)
    private BigDecimal precoVenda = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Version
    private Long version;

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
