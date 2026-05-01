package com.dinoco.oficina.helper;

import com.dinoco.oficina.dto.VeiculoRequestDto;

public class VeiculoRequestDtoBuilder {

    private String placa = "KLA2024";
    private String marca = "Toyota";
    private String modelo = "Corolla";
    private Integer anoFabricacao = 2024;
    private Integer anoModelo = 2024;
    private String cor = "Preto";
    private String chassi = "9BRZZZ";
    private String motor = "2.0";

    private VeiculoRequestDtoBuilder() {}

    public static VeiculoRequestDtoBuilder umRequest() {
        return new VeiculoRequestDtoBuilder();
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
