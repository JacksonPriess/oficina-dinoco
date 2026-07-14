package com.dinoco.oficina.estoque.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "saldo_estoque")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class SaldoEstoqueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "produto_id", nullable = false, unique = true)
    private Long produtoId;

    @Column(name = "quantidade_real", nullable = false)
    private BigDecimal quantidadeReal;

    @Column(name = "quantidade_reservada", nullable = false)
    private BigDecimal quantidadeReservada;

    @Version
    private Long versao;

}