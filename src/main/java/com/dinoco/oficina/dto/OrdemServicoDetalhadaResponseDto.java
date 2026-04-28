package com.dinoco.oficina.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrdemServicoDetalhadaResponseDto(
        Long id,
        String codigoRastreio,
        String nomeCliente,
        String placaVeiculo,
        String reclamacaoCliente,
        String laudoTecnico,
        BigDecimal valorTotalServicos,
        BigDecimal valorTotalProdutos,
        BigDecimal valorTotalOS,
        String status,
        LocalDateTime dataEntrada,
        List<ItemServicoDetalheDto> servicos,
        List<ItemProdutoDetalheDto> produtos
) {}