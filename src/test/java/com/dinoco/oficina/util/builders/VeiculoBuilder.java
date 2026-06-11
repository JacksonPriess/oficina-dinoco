package com.dinoco.oficina.util.builders;


import com.dinoco.oficina.veiculo.domain.Veiculo;

public class VeiculoBuilder {

    private Veiculo veiculo;

    private VeiculoBuilder() {
        //this.veiculo = new Veiculo();
    }

    public static VeiculoBuilder umVeiculo() {
        VeiculoBuilder builder = new VeiculoBuilder();
        builder.veiculo.setId(1L);
        builder.veiculo.setPlaca("FOC2012");
        builder.veiculo.setMarca("Ford");
        builder.veiculo.setModelo("Focus");
        builder.veiculo.setAnoFabricacao(2012);
        builder.veiculo.setAnoModelo(2013);
        builder.veiculo.setCor("Prata");
        builder.veiculo.setChassi("9BFZZZABC12345678");
        builder.veiculo.setMotor("2.0 Duratec");
        builder.veiculo.setAtivo(true);
        return builder;
    }

    public VeiculoBuilder comPlaca(String placa) {
        this.veiculo.setPlaca(placa);
        return this;
    }

    public VeiculoBuilder comId(Long id) {
        this.veiculo.setId(id);
        return this;
    }

    public Veiculo build() {
        return this.veiculo;
    }
}
