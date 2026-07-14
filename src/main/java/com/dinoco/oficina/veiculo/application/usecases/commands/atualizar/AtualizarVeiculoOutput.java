package com.dinoco.oficina.veiculo.application.usecases.commands.atualizar;

public record AtualizarVeiculoOutput(
        Long id,
        String placa,
        String marca,
        String modelo,
        Integer anoFabricacao,
        Integer anoModelo,
        String cor,
        String chassi,
        String motor,
        Boolean ativo
) {}
