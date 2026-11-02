package com.dinoco.oficina.estoque.infrastructure.events;
import com.dinoco.oficina.estoque.application.usecases.commands.inicializar.InicializarEstoqueUseCase;
import com.dinoco.oficina.estoque.infrastructure.config.ProdutoRabbitConfig;
import com.dinoco.oficina.shared.events.ProdutoCadastradoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
public class ProdutoCadastradoListener {

    private final InicializarEstoqueUseCase useCase;
    private final JsonMapper json;

    public ProdutoCadastradoListener(InicializarEstoqueUseCase useCase, JsonMapper json) {
        this.useCase = useCase;
        this.json = json;
    }

    @RabbitListener(queues = ProdutoRabbitConfig.QUEUE)
    public void receber(Message message) {
        var messageId = message.getMessageProperties().getMessageId();
        try {
            var evento = json.readValue(message.getBody(), ProdutoCadastradoEvent.class);

            log.debug(
                    "evento=produto_cadastrado_recebido eventoId={} produtoId={} quantidadeInicial={}",
                    evento.eventId(),
                    evento.produtoId(),
                    evento.quantidade()
            );

            useCase.executar(evento);
            // AUTO confirma somente após retorno: a transação já foi confirmada pelo decorator.
            // Não capturar e esconder exceções: precisam chegar ao container para retry/DLQ.

            log.info(
                    "evento=estoque_inicial_processamento_sucesso eventoId={} produtoId={} quantidadeInicial={}",
                    evento.eventId(),
                    evento.produtoId(),
                    evento.quantidade()
            );
        } catch (RuntimeException e) {
            log.warn("evento=estoque_inicial_processamento_falha messageId={}",
                    messageId, e);

            throw e;
        }

    }
}
