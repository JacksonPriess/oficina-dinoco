package com.dinoco.oficina.veiculo.infrastructure.web.dto;

public record VeiculoResponseDto(
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