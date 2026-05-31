package com.dinoco.oficina.veiculo.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Veiculo {

    private Long id;
    private String placa;
    private String marca;
    private String modelo;
    private Integer anoFabricacao;
    private Integer anoModelo;
    private String cor;
    private String chassi;
    private String motor;
    private Boolean ativo;
    private final LocalDateTime dataCriacao;

    public Veiculo(String placa, String marca, String modelo, Integer anoFabricacao,
                    Integer anoModelo, String cor, String chassi, String motor) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anoFabricacao = anoFabricacao;
        this.anoModelo = anoModelo;
        this.cor = cor;
        this.chassi = chassi;
        this.motor = motor;
        this.dataCriacao = LocalDateTime.now();
        this.ativo = true;
    }

    public Veiculo(Long id, String placa, String marca, String modelo, Integer anoFabricacao,
                   Integer anoModelo, String cor, String chassi, String motor, Boolean ativo,
                   LocalDateTime dataCriacao) {
        this.id = id;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anoFabricacao = anoFabricacao;
        this.anoModelo = anoModelo;
        this.cor = cor;
        this.chassi = chassi;
        this.motor = motor;
        this.ativo = ativo;
        this.dataCriacao = dataCriacao;
    }

    public void desativar() {
        this.ativo = false;
    }

    public void atualizar(String placa, String marca, String modelo, Integer anoFabricacao,
                        Integer anoModelo, String cor, String chassi, String motor) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anoFabricacao = anoFabricacao;
        this.anoModelo = anoModelo;
        this.cor = cor;
        this.chassi = chassi;
        this.motor = motor;
    }



}
