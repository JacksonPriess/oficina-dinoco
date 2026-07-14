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

        validarVeiculo(placa, marca, modelo);

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

    private void validarVeiculo(String placa, String marca, String modelo) {

        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("A placa não pode estar vazia.");
        }

        String regex = "^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$";
        if (!placa.matches(regex)) {
            throw new IllegalArgumentException("Placa inválida. Digite apenas letras e números no padrão antigo (ABC1234) ou mercosul (ABC1D23), sem hífen.");
        }

        if (marca == null || marca.trim().isEmpty()) {
            throw new IllegalArgumentException("A marca não pode estar vazia.");
        }

        if (modelo == null || modelo.trim().isEmpty()) {
            throw new IllegalArgumentException("O modelo não pode estar vazia.");
        }
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

    public void atualizar(String placa, String marca, String modelo, Integer anoFabricacao,
                        Integer anoModelo, String cor, String chassi, String motor) {

        validarVeiculo(placa, marca, modelo);

        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anoFabricacao = anoFabricacao;
        this.anoModelo = anoModelo;
        this.cor = cor;
        this.chassi = chassi;
        this.motor = motor;
    }

    public void desativar() {
        this.ativo = false;
    }

}
