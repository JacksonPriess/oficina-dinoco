package com.dinoco.oficina.ordemservico.infrastructure.web.dto;

import java.math.BigDecimal;

public record OrdemServicoResponseDto(
        Long id,
        String codigoRastreio,
        Long clienteId,
        Long veiculoId,
        String reclamacaoCliente,
        Integer quilometragemEntrada,
        String laudoTecnico,
        BigDecimal valorTotalServicos,
        BigDecimal valorTotalProdutos,
        BigDecimal valorDesconto,
        BigDecimal valorTotalOS,
        String status
) {}