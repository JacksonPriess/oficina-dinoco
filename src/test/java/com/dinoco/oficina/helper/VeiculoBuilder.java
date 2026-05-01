package com.dinoco.oficina.helper;

import com.dinoco.oficina.entity.Veiculo;

public class VeiculoBuilder {

    private Long id;
    private String placa = "FOC2012";
    private String marca = "Ford";
    private String modelo = "Focus";
    private Integer anoFabricacao = 2012;
    private Integer anoModelo = 2013;
    private String cor = "Prata";
    private String chassi = "9BFZZZABC12345678";
    private String motor = "2.0 Duratec";
    private Boolean ativo = true;

    private VeiculoBuilder() {}

    public static VeiculoBuilder umVeiculo() {
        return new VeiculoBuilder();
    }

    public Veiculo build() {
        Veiculo veiculo = new Veiculo();
        veiculo.setId(this.id);
        veiculo.setPlaca(this.placa);
        veiculo.setMarca(this.marca);
        veiculo.setModelo(this.modelo);
        veiculo.setAnoFabricacao(this.anoFabricacao);
        veiculo.setAnoModelo(this.anoModelo);
        veiculo.setCor(this.cor);
        veiculo.setChassi(this.chassi);
        veiculo.setMotor(this.motor);
        veiculo.setAtivo(this.ativo);
        return veiculo;
    }
}
