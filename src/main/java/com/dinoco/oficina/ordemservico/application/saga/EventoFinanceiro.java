package com.dinoco.oficina.ordemservico.application.saga;

import java.math.BigDecimal;
import java.util.UUID;

public record EventoFinanceiro(UUID eventId, String tipo, UUID sagaId, Long osId, UUID pagamentoId,
                               BigDecimal valor, String moeda, String provedorId, String estornoId, String ocorridoEm) {}
