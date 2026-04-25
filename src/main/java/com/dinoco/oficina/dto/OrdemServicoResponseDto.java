package com.dinoco.oficina.dto;

import java.math.BigDecimal;

public record OrdemServicoResponseDto(
        Long id,
        String codigoRastreio,
        Long clienteId,
        String nomeCliente,
        Long veiculoId,
        String placaVeiculo,
        String reclamacaoCliente,
        Integer quilometragemEntrada,
        String laudoTecnico,
        BigDecimal valorTotalServicos,
        BigDecimal valorTotalProdutos,
        BigDecimal valorDesconto,
        BigDecimal valorTotalOS,
        String status
) {}