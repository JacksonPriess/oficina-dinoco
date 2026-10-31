package com.dinoco.oficina.estoque.infrastructure.persistence;

import com.dinoco.oficina.estoque.application.gateways.InicializacaoEstoqueGateway;
import com.dinoco.oficina.shared.events.ProdutoCadastradoEvent;
import java.math.BigDecimal;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InicializacaoEstoqueGatewayImpl implements InicializacaoEstoqueGateway {

    private final JdbcTemplate jdbc;

    public InicializacaoEstoqueGatewayImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean registrarSeNovo(ProdutoCadastradoEvent evento) {
        int inseridos = jdbc.update("""
            INSERT INTO estoque_inicializacao(event_id, produto_id, quantidade)
            VALUES (?, ?, ?) ON CONFLICT (event_id) DO NOTHING
            """, evento.eventId(), evento.produtoId(), normalizar(evento.quantidade()));
        if (inseridos == 1)
            return true;

        Boolean mesmosDados = jdbc.queryForObject("""
            SELECT produto_id = ? AND quantidade = ?
            FROM estoque_inicializacao WHERE event_id = ?
            """, Boolean.class, evento.produtoId(), normalizar(evento.quantidade()), evento.eventId());

        if (!Boolean.TRUE.equals(mesmosDados))
            throw new IllegalArgumentException("eventId reutilizado com conteúdo diferente.");

        return false;
    }
    private BigDecimal normalizar(BigDecimal quantidade) {
        return quantidade == null ? BigDecimal.ZERO : quantidade;
    }
}
