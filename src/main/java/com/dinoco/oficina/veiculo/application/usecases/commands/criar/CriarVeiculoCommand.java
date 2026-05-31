package com.dinoco.oficina.veiculo.application.usecases.commands.criar;

public record CriarVeiculoCommand(
        String placa,
        String marca,
        String modelo,
        Integer anoFabricacao,
        Integer anoModelo,
        String cor,
        String chassi,
        String motor
) {}