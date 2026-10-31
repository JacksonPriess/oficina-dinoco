package com.dinoco.oficina.estoque.infrastructure.events;
import com.dinoco.oficina.estoque.application.usecases.commands.inicializar.InicializarEstoqueUseCase;
import com.dinoco.oficina.estoque.infrastructure.config.ProdutoRabbitConfig;
import com.dinoco.oficina.shared.events.ProdutoCadastradoEvent;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

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
        var evento = json.readValue(message.getBody(), ProdutoCadastradoEvent.class);
        useCase.executar(evento);
        // AUTO confirma somente após retorno: a transação já foi confirmada pelo decorator.
        // Não capturar e esconder exceções: precisam chegar ao container para retry/DLQ.
    }
}
