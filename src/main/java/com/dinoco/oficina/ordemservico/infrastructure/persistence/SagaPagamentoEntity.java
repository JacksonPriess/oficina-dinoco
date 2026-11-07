package com.dinoco.oficina.ordemservico.infrastructure.persistence;

import com.dinoco.oficina.ordemservico.domain.models.SagaPagamento.Estado;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "os_pagamento_saga")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "sagaId")
public class SagaPagamentoEntity {

    @Id
    @Column(name = "saga_id")
    private UUID sagaId;

    @Column(name = "os_id", nullable = false, unique = true)
    private Long osId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Estado estado;

    @Column(name = "pagamento_id")
    private UUID pagamentoId;

    @Column(name = "provedor_id", length = 100)
    private String provedorId;

    @Column(name = "estorno_id", length = 100)
    private String estornoId;
}
