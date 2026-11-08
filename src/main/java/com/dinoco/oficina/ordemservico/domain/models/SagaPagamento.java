package com.dinoco.oficina.ordemservico.domain.models;

import com.dinoco.oficina.ordemservico.domain.exceptions.RegraNegocioOSException;

import java.math.BigDecimal;
import java.util.UUID;

public record SagaPagamento(UUID sagaId, Long osId, BigDecimal valor, Estado estado,
                             UUID pagamentoId, String provedorId, String estornoId) {

    public enum Estado {
        SOLICITADA, PENDENTE, APROVADA, ESTORNO_SOLICITADO, ESTORNADA, ENTREGUE
    }

    public static SagaPagamento iniciar(Long osId, BigDecimal valor) {
        return new SagaPagamento(UUID.randomUUID(), osId, valor, Estado.SOLICITADA, null, null, null);
    }

    public SagaPagamento receberResultado(String tipo, UUID pagamento, String provedor, String estorno) {
        if (pagamento == null || provedor == null || provedor.isBlank()
                || (pagamentoId != null && (!pagamentoId.equals(pagamento) || !provedorId.equals(provedor)))) {
            throw new IllegalArgumentException("Identificadores do pagamento incompatíveis com a Saga.");
        }
        return switch (tipo) {
            case "pagamento.criado.v1" -> estado == Estado.SOLICITADA
                    ? atualizar(Estado.PENDENTE, pagamento, provedor, null) : this;
            case "pagamento.aprovado.v1" -> estado == Estado.SOLICITADA || estado == Estado.PENDENTE
                    ? atualizar(Estado.APROVADA, pagamento, provedor, null) : this;
            case "pagamento.estornado.v1" -> confirmarEstorno(pagamento, provedor, estorno);
            default -> throw new IllegalArgumentException("Evento financeiro desconhecido.");
        };
    }

    private SagaPagamento confirmarEstorno(UUID pagamento, String provedor, String estorno) {
        if (estorno == null || estorno.isBlank()) {
            throw new IllegalArgumentException("Confirmação de estorno sem identificador.");
        }
        if (estado == Estado.ESTORNADA && estorno.equals(estornoId)) return this;
        if (estado != Estado.ESTORNO_SOLICITADO) {
            throw new IllegalStateException("Estorno recebido sem solicitação da Saga.");
        }
        return atualizar(Estado.ESTORNADA, pagamento, provedor, estorno);
    }

    public SagaPagamento solicitarEstorno() {
        if (estado == Estado.ESTORNO_SOLICITADO || estado == Estado.ESTORNADA) return this;
        if (estado != Estado.APROVADA) {
            throw new RegraNegocioOSException("Somente um pagamento aprovado de OS ainda não entregue pode ser estornado.");
        }
        return atualizar(Estado.ESTORNO_SOLICITADO, pagamentoId, provedorId, null);
    }

    public SagaPagamento registrarEntrega() {
        if (estado != Estado.APROVADA) {
            throw new RegraNegocioOSException("A entrega exige pagamento aprovado e sem estorno.");
        }
        return atualizar(Estado.ENTREGUE, pagamentoId, provedorId, null);
    }

    private SagaPagamento atualizar(Estado novoEstado, UUID pagamento, String provedor, String estorno) {
        return new SagaPagamento(sagaId, osId, valor, novoEstado, pagamento, provedor, estorno);
    }
}
