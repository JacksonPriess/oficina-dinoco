package com.dinoco.oficina.ordemservico.infrastructure.web.dto;

import com.dinoco.oficina.ordemservico.domain.models.SagaPagamento;

import java.math.BigDecimal;
import java.util.UUID;

public record PagamentoResponse(UUID sagaId, Long osId, BigDecimal valor, SagaPagamento.Estado estado,
                                UUID pagamentoId, String provedorId, String estornoId) {}
