package com.dinoco.oficina.estoque.application.listeners;

import com.dinoco.oficina.estoque.application.usecases.commands.inicializar.InicializarEstoqueUseCase;
import com.dinoco.oficina.estoque.infrastructure.events.ProdutoCadastradoListener;
import com.dinoco.oficina.shared.events.ProdutoCadastradoEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoCadastradoListenerTest {

    private static final UUID EVENT_ID =
            UUID.fromString("802de405-d459-476c-8260-7008560bc630");

    @Mock
    private InicializarEstoqueUseCase useCase;

    private ProdutoCadastradoListener listener;

    @BeforeEach
    void configurar() {
        // Mapper real: o teste verifica a conversão do JSON recebido.
        listener = new ProdutoCadastradoListener(useCase, JsonMapper.builder().build());
    }

    @Test
    void deveDesserializarEventoEPreservarIdentificadoresEQuantidadeDecimal() {
        Message mensagem = mensagem("""
                {
                  "eventId": "802de405-d459-476c-8260-7008560bc630",
                  "produtoId": 123,
                  "quantidade": 10.125
                }
                """);

        listener.receber(mensagem);

        var captor = ArgumentCaptor.forClass(ProdutoCadastradoEvent.class);
        verify(useCase).executar(captor.capture());
        ProdutoCadastradoEvent evento = captor.getValue();
        assertAll(
                () -> assertEquals(EVENT_ID, evento.eventId()),
                () -> assertEquals(Long.valueOf(123L), evento.produtoId()),
                () -> assertEquals(new BigDecimal("10.125"), evento.quantidade())
        );
        verifyNoMoreInteractions(useCase);
    }

    @Test
    void deveEncaminharQuantidadeNulaSemConverterParaZero() {
        Message mensagem = mensagem("""
                {
                  "eventId": "802de405-d459-476c-8260-7008560bc630",
                  "produtoId": 123,
                  "quantidade": null
                }
                """);

        listener.receber(mensagem);

        var captor = ArgumentCaptor.forClass(ProdutoCadastradoEvent.class);
        verify(useCase).executar(captor.capture());
        assertNull(captor.getValue().quantidade());
        verifyNoMoreInteractions(useCase);
    }

    @Test
    void deveEncaminharQuantidadeZeroAoCasoDeUso() {
        Message mensagem = mensagem("""
                {
                  "eventId": "802de405-d459-476c-8260-7008560bc630",
                  "produtoId": 123,
                  "quantidade": 0
                }
                """);

        listener.receber(mensagem);

        verify(useCase).executar(new ProdutoCadastradoEvent(EVENT_ID, 123L, BigDecimal.ZERO));
        verifyNoMoreInteractions(useCase);
    }

    @Test
    void devePropagarErroDeJsonMalformadoSemExecutarCasoDeUso() {
        Message mensagem = mensagem("{ json-invalido");

        assertThrows(JacksonException.class, () -> listener.receber(mensagem));

        verifyNoInteractions(useCase);
    }

    @Test
    void deveRejeitarTipoIncompativelSemExecutarCasoDeUso() {
        Message mensagem = mensagem("""
                {
                  "eventId": "802de405-d459-476c-8260-7008560bc630",
                  "produtoId": "nao-e-um-numero",
                  "quantidade": 10
                }
                """);

        assertThrows(JacksonException.class, () -> listener.receber(mensagem));

        verifyNoInteractions(useCase);
    }

    @Test
    void devePropagarMesmaExcecaoDoCasoDeUsoParaOContainer() {
        Message mensagem = mensagem("""
                {
                  "eventId": "802de405-d459-476c-8260-7008560bc630",
                  "produtoId": 123,
                  "quantidade": 10
                }
                """);
        var falha = new IllegalStateException("Banco indisponível");
        doThrow(falha).when(useCase).executar(any(ProdutoCadastradoEvent.class));

        IllegalStateException recebida = assertThrows(
                IllegalStateException.class, () -> listener.receber(mensagem)
        );

        assertSame(falha, recebida);
        verify(useCase).executar(new ProdutoCadastradoEvent(EVENT_ID, 123L, BigDecimal.TEN));
        verifyNoMoreInteractions(useCase);
    }

    private Message mensagem(String json) {
        var properties = new MessageProperties();
        properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        properties.setContentEncoding(StandardCharsets.UTF_8.name());
        return new Message(json.getBytes(StandardCharsets.UTF_8), properties);
    }
}
