package com.dinoco.oficina.ordemservico.infrastructure.web;

import com.dinoco.oficina.ordemservico.application.saga.OrquestradorPagamento;
import com.dinoco.oficina.ordemservico.domain.models.SagaPagamento;
import com.dinoco.oficina.ordemservico.infrastructure.web.dto.PagamentoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/ordens-servico/{osId}/pagamento")
@Tag(name = "Pagamento da OS", description = "Consulta da Saga e solicitação de compensação financeira")
public class PagamentoOsSpringWebController {

    private final OrquestradorPagamento orquestradorPagamento;
    private final TransactionTemplate transactionTemplate;

    public PagamentoOsSpringWebController(OrquestradorPagamento orquestrador, PlatformTransactionManager manager) {
        this.orquestradorPagamento = orquestrador;
        this.transactionTemplate = new TransactionTemplate(manager);
    }

    @Operation(summary = "Consultar o estado financeiro da OS")
    @GetMapping
    public PagamentoResponse consultar(@PathVariable Long osId) {
        var saga = orquestradorPagamento.consultar(osId);
        return new PagamentoResponse(saga.sagaId(), saga.osId(), saga.valor(), saga.estado(), saga.pagamentoId(),
                saga.provedorId(), saga.estornoId());
    }

    @Operation(summary = "Solicitar estorno antes da entrega", description = "Operação assíncrona. Consulte a Saga para confirmar o estorno.")
    @PostMapping("/estorno")
    public ResponseEntity<Void> estornar(@PathVariable Long osId) {
        transactionTemplate.executeWithoutResult(status -> orquestradorPagamento.solicitarEstorno(osId));
        return ResponseEntity.accepted().build();
    }

    //TODO - Nova cobrança, para quando ocorrer estorno e o cliente quiser pagar novamente. A cobrança anterior deve ser cancelada e uma nova criada.
    /*
    @Operation(summary = "Solicitar nova cobrança", description = "Operação assíncrona. Consulte a Saga para confirmar a nova cobrança.")
    @PostMapping("/nova-cobranca")
     */


}
