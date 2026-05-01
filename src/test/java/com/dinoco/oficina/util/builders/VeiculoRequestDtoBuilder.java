package com.dinoco.oficina.util.builders;

import com.dinoco.oficina.dto.VeiculoRequestDto;

public class VeiculoRequestDtoBuilder {

    private String placa;
    private String marca;
    private String modelo;
    private Integer anoFabricacao;
    private Integer anoModelo;
    private String cor;
    private String chassi;
    private String motor;

    private VeiculoRequestDtoBuilder() {}

    public static VeiculoRequestDtoBuilder umRequest() {
        VeiculoRequestDtoBuilder builder = new VeiculoRequestDtoBuilder();
        builder.placa = "FOC2012";
        builder.marca = "Ford";
        builder.modelo = "Focus";
        builder.anoFabricacao = 2012;
        builder.anoModelo = 2013;
        builder.cor = "Prata";
        builder.chassi = "9BFZZZABC12345678";
        builder.motor = "2.0 Duratec";
        return builder;
    }

    public VeiculoRequestDtoBuilder comPlaca(String placa) {
        this.placa = placa;
        return this;
    }

    public VeiculoRequestDtoBuilder comCor(String cor) {
        this.cor = cor;
        return this;
    }

    public VeiculoRequestDto build() {
        return new VeiculoRequestDto(
                this.placa,
                this.marca,
                this.modelo,
                this.anoFabricacao,
                this.anoModelo,
                this.cor,
                this.chassi,
                this.motor
        );
    }
}
