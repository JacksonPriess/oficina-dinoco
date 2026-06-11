package com.dinoco.oficina.ordemservico.application.usecases.queries.buscarpornumero;

import java.math.BigDecimal;

public record BuscarOSPorCodigoRastreioOuput(
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
        String status)
{}