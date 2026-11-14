package com.dinoco.oficina.ordemservico.infrastructure.jobs;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.jdbc.core.*;
import org.springframework.transaction.*;
import java.sql.ResultSet;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PagamentoOutboxWorkerTest {
    private final JdbcTemplate jdbc = mock(JdbcTemplate.class);
    private final RabbitTemplate rabbit = mock(RabbitTemplate.class);
    private final PlatformTransactionManager manager = mock(PlatformTransactionManager.class);
    private final TransactionStatus transaction = mock(TransactionStatus.class);
    private final UUID eventId = UUID.randomUUID();
    private PagamentoOutboxWorker worker;

    @BeforeEach void configurar() {
        when(manager.getTransaction(any())).thenReturn(transaction);
        worker = new PagamentoOutboxWorker(jdbc, rabbit, manager);
    }
    private void pendente() throws Exception {
        var rs = mock(ResultSet.class);
        when(rs.getObject(1, UUID.class)).thenReturn(eventId);
        when(rs.getString(2)).thenReturn("pagamento.criar.v1");
        when(rs.getString(3)).thenReturn("{\"osId\":42}");
        when(jdbc.query(anyString(), any(RowMapper.class))).thenAnswer(i -> List.of(i.<RowMapper<?>>getArgument(1).mapRow(rs, 0)));
    }
    @Test void semPendenciasNaoPublica() {
        when(jdbc.query(anyString(), any(RowMapper.class))).thenReturn(List.of());
        worker.publicar();
        verifyNoInteractions(rabbit);
        verify(manager).commit(transaction);
    }
    @Test void marcarPublicadoSomenteAposConfirmacao() throws Exception {
        pendente();
        doAnswer(i -> {
            Message message = i.getArgument(2);
            assertThat(message.getBody()).asString().isEqualTo("{\"osId\":42}");
            assertThat(message.getMessageProperties().getMessageId()).isEqualTo(eventId.toString());
            assertThat(message.getMessageProperties().getDeliveryMode()).isEqualTo(MessageDeliveryMode.PERSISTENT);
            assertThat(message.getMessageProperties().getContentType()).isEqualTo("application/json");
            verify(jdbc, never()).update(anyString(), any(UUID.class));
            i.<CorrelationData>getArgument(3).getFuture().complete(new CorrelationData.Confirm(true, null));
            return null;
        }).when(rabbit).send(eq("oficina.financeiro"), eq("pagamento.criar.v1"), any(Message.class), any(CorrelationData.class));
        worker.publicar();
        verify(jdbc).update(contains("publicado_em=CURRENT_TIMESTAMP"), eq(eventId));
        verify(manager).commit(transaction);
    }
    @ParameterizedTest @ValueSource(strings = {"nack", "sem-fila", "conexao"})
    void falhaMantemPendenteEAgendaNovaTentativa(String falha) throws Exception {
        pendente();
        doAnswer(i -> {
            if (falha.equals("conexao")) throw new IllegalStateException("Broker indisponível");
            CorrelationData data = i.getArgument(3);
            if (falha.equals("sem-fila")) data.setReturned(new ReturnedMessage(i.getArgument(2), 312, "NO_ROUTE", "oficina.financeiro", "pagamento.criar.v1"));
            data.getFuture().complete(new CorrelationData.Confirm(!falha.equals("nack"), null));
            return null;
        }).when(rabbit).send(anyString(), anyString(), any(Message.class), any(CorrelationData.class));
        worker.publicar();
        verify(jdbc).update(contains("tentativas=tentativas+1"), eq(eventId));
        verify(jdbc, never()).update(contains("publicado_em=CURRENT_TIMESTAMP"), any(UUID.class));
    }
    @Test void falhaDeConsultaFazRollback() {
        when(jdbc.query(anyString(), any(RowMapper.class))).thenThrow(new IllegalStateException("Banco indisponível"));
        worker.publicar();
        verify(manager).rollback(transaction);
        verifyNoInteractions(rabbit);
    }
}
