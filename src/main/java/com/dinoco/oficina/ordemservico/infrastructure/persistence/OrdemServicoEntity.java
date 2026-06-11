package com.dinoco.oficina.ordemservico.infrastructure.persistence;

import com.dinoco.oficina.ordemservico.domain.enums.StatusOS;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ordem_servico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class OrdemServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_rastreio", nullable = false, unique = true)
    private String codigoRastreio;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "veiculo_id", nullable = false)
    private Long veiculoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOS status = StatusOS.RECEBIDA;

    @Column(name = "quilometragem_entrada", nullable = false)
    private Integer quilometragemEntrada;

    @Column(name = "reclamacao_cliente", columnDefinition = "TEXT", nullable = false)
    private String reclamacaoCliente;

    @Column(name = "laudo_tecnico", columnDefinition = "TEXT")
    private String laudoTecnico;

    @Column(name = "valor_total_servicos", nullable = false)
    private BigDecimal valorTotalServicos = BigDecimal.ZERO;

    @Column(name = "valor_total_produtos", nullable = false)
    private BigDecimal valorTotalProdutos = BigDecimal.ZERO;

    @Column(name = "valor_desconto", nullable = false)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Column(name = "valor_total_os", nullable = false)
    private BigDecimal valorTotalOS = BigDecimal.ZERO;

    @Column(name = "data_entrada", nullable = false)
    private LocalDateTime dataEntrada = LocalDateTime.now();

    @Column(name = "data_saida")
    private LocalDateTime dataSaida;

    @Column(name = "data_reprovacao")
    private LocalDateTime dataReprovacao;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ItemOSServicoEntity> itensServico = new HashSet<>();

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ItemOSProdutoEntity> itensProduto = new HashSet<>();

}
