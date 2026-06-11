package com.dinoco.oficina.ordemservico.application.usecases.queries.buscarporid;

import java.math.BigDecimal;

public record BuscarOSPorIdOuput(
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
        String status)
{}