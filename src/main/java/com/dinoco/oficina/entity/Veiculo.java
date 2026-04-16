package com.dinoco.oficina.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "veiculo")
public class Veiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relação N:1 - Muitos veículos podem pertencer a um Cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false, length = 8, unique = true)
    private String placa;

    @Column(nullable = false, length = 50)
    private String marca;

    @Column(nullable = false, length = 100)
    private String modelo;

    @Column(name = "ano_fabricacao")
    private Integer anoFabricacao;

    @Column(name = "ano_modelo")
    private Integer anoModelo;

    @Column(length = 30)
    private String cor;

    @Column(length = 17)
    private String chassi;

    @Column(length = 50)
    private String motor;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
    }
}
