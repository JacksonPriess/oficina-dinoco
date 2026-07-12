package com.dinoco.oficina.ordemservico.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FilaTrabalhosDetalhesResponseDto(
         Long id,
         String codigoRastreio,
         Long clienteId,
         Long veiculoId,
         String status,
         LocalDateTime dataEntrada,
         BigDecimal valorTotalOS) {

}