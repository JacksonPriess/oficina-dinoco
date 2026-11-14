package com.dinoco.oficina.ordemservico.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BuscarOSPorIdResponseDto(
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
        List<BuscarOSPorIdItemServicoResponseDto> itensServico,
        List<BuscarOSPorIdItemProdutoResponseDto> itensProduto
) {}