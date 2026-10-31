package com.dinoco.oficina.estoque.infrastructure.config;
import com.dinoco.oficina.estoque.application.gateways.*;
import com.dinoco.oficina.estoque.application.usecases.commands.inicializar.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class InicializacaoEstoqueConfig {

    @Bean
    public InicializarEstoqueUseCase inicializarEstoqueUseCase(EstoqueCommandGateway estoque, InicializacaoEstoqueGateway inicializacao, PlatformTransactionManager transactionManager) {

        var handler = new InicializarEstoqueHandler(estoque, inicializacao);

        var transaction = new TransactionTemplate(transactionManager);
        return evento -> transaction.executeWithoutResult(status -> handler.executar(evento));
    }
}
