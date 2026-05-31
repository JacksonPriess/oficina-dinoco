package com.dinoco.oficina.veiculo.application.usecases.queries.buscarporid;

import java.time.LocalDateTime;

public record BuscarVeiculoPorIdOutput(
        Long id,
        String placa,
        String marca,
        String modelo,
        Integer anoFabricacao,
        Integer anoModelo,
        String cor,
        String chassi,
        String motor,
        Boolean ativo,
        LocalDateTime dataCriacao
) {}
