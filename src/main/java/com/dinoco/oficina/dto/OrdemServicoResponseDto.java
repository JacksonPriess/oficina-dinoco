package com.dinoco.oficina.dto;

public record OrdemServicoResponseDto(
        Long id,
        String codigoRastreio,
        Long clienteId,
        String nomeCliente,
        Long veiculoId,
        String placaVeiculo,
        String reclamacaoCliente
) {}
