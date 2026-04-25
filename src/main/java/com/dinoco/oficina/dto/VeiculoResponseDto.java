package com.dinoco.oficina.dto;

public record VeiculoResponseDto(
    Long id,
    Long clienteId,
    String nomeCliente,
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