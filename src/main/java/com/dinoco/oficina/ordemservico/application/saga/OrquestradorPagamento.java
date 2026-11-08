package com.dinoco.oficina.ordemservico.application.saga;

import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.ordemservico.application.gateways.ComandoFinanceiroGateway;
import com.dinoco.oficina.ordemservico.application.gateways.SagaPagamentoGateway;
import com.dinoco.oficina.ordemservico.domain.exceptions.RegraNegocioOSException;
import com.dinoco.oficina.ordemservico.domain.models.SagaPagamento;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OrquestradorPagamento {

    private final SagaPagamentoGateway sagas;
    private final ComandoFinanceiroGateway comandos;

    public OrquestradorPagamento(SagaPagamentoGateway sagas, ComandoFinanceiroGateway comandos) {
        this.sagas = sagas;
        this.comandos = comandos;
    }

    // O chamador mantém a OS bloqueada e grava a finalização na mesma transação.
    public void iniciar(Long osId, BigDecimal valor) {
        var valorCobrado = valor.setScale(2, RoundingMode.HALF_UP);
        if (valorCobrado.signum() == 0 || sagas.buscarPorOsId(osId).isPresent()) return;
        var saga = SagaPagamento.iniciar(osId, valorCobrado);
        sagas.salvar(saga);
        comandos.criarPagamento(saga);
    }

    public SagaPagamento consultar(Long osId) {
        return sagas.buscarPorOsId(osId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Saga de pagamento não encontrada para a OS."));
    }

    public void autorizarEntrega(Long osId, BigDecimal valor) {
        var valorCobrado = valor.setScale(2, RoundingMode.HALF_UP);
        if (valorCobrado.signum() == 0) return;
        var saga = sagas.bloquearPorOsId(osId)
                .orElseThrow(() -> new RegraNegocioOSException("A entrega exige pagamento aprovado."));
        if (saga.valor().compareTo(valorCobrado) != 0) {
            throw new RegraNegocioOSException("O pagamento não corresponde ao valor da OS.");
        }
        sagas.salvar(saga.registrarEntrega());
    }

    public void solicitarEstorno(Long osId) {
        var atual = sagas.bloquearPorOsId(osId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Saga de pagamento não encontrada para a OS."));
        var alterada = atual.solicitarEstorno();
        if (alterada.equals(atual)) return;
        sagas.salvar(alterada);
        comandos.estornarPagamento(alterada);
    }

    public SagaPagamento receber(EventoFinanceiro evento) {
        if (evento.eventId() == null || evento.osId() == null || evento.sagaId() == null || evento.tipo() == null) {
            throw new IllegalArgumentException("Evento financeiro sem identificadores obrigatórios.");
        }
        var atual = sagas.bloquearPorOsId(evento.osId())
                .orElseThrow(() -> new IllegalArgumentException("Evento recebido para Saga inexistente."));
        if (!atual.sagaId().equals(evento.sagaId()) || !"BRL".equals(evento.moeda())
                || evento.valor() == null || atual.valor().compareTo(evento.valor()) != 0) {
            throw new IllegalArgumentException("Evento incompatível com a cobrança da OS.");
        }
        var alterada = atual.receberResultado(evento.tipo(), evento.pagamentoId(), evento.provedorId(), evento.estornoId());
        if (!alterada.equals(atual)) sagas.salvar(alterada);
        return alterada;
    }
}
