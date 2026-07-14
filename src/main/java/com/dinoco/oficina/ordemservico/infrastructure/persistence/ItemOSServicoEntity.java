package com.dinoco.oficina.ordemservico.infrastructure.persistence;

import com.dinoco.oficina.ordemservico.domain.enums.StatusItemServico;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "item_os_servico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ItemOSServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "os_id", nullable = false)
    private OrdemServicoEntity ordemServico;

    @Column(name = "servico_id", nullable = false)
    private Long servicoId;

    @Column(name = "funcionario_id")
    private Long mecanicoId;

    @Column(name = "valor_cobrado", nullable = false)
    private BigDecimal valorCobrado = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_item", nullable = false)
    private StatusItemServico statusItem = StatusItemServico.PENDENTE;

    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

}