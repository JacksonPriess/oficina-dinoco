package com.dinoco.oficina.entity;

import com.dinoco.oficina.enums.StatusOS;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "ordem_servico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_rastreio", nullable = false, unique = true)
    private String codigoRastreio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

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

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ItemOSServico> itensServico = new HashSet<>();

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ItemOSProduto> itensProduto = new HashSet<>();

    @PrePersist
    private void prePersist() {
        if (this.codigoRastreio == null) {
            this.codigoRastreio = "OS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    public OrdemServico(Cliente cliente, Veiculo veiculo, Integer quilometragem, String reclamacao) {
        this.cliente = cliente;
        this.veiculo = veiculo;
        this.quilometragemEntrada = quilometragem;
        this.reclamacaoCliente = reclamacao;
    }
}
