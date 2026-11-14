package com.dinoco.oficina.ordemservico.application.usecases.queries.buscarporid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BuscarOSPorIdOuput(
        Long osId,
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
        LocalDateTime dataEntrada,
        LocalDateTime dataSaida,
        LocalDateTime dataReprovacao,
        LocalDateTime dataInicioDiagnostico,
        LocalDateTime dataFinalDiagnostico,
        LocalDateTime dataInicioExecucao,
        LocalDateTime dataFinalExecucao,
        String status,
        List<BuscarOSPorIdItemServicoOutput> itensServico,
        List<BuscarOSPorIdItemProdutoOutput> itensProduto)
{}