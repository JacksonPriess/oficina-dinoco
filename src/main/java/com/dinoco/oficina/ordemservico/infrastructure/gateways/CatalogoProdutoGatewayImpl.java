package com.dinoco.oficina.ordemservico.infrastructure.gateways;

import com.dinoco.oficina.ordemservico.application.gateways.CatalogoProdutoGateway;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class CatalogoProdutoGatewayImpl implements CatalogoProdutoGateway {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CatalogoProdutoGatewayImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal buscarPrecoVendaAtual(Long produtoId) {
            String sql = """
            SELECT preco_venda
            FROM produto
            WHERE id = :produtoId
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("produtoId", produtoId);

        try {
            BigDecimal precoVenda = jdbcTemplate.queryForObject(
                    sql,
                    params,
                    BigDecimal.class
            );

            return precoVenda != null ? precoVenda : BigDecimal.ZERO;
        } catch (EmptyResultDataAccessException e) {
            return BigDecimal.ZERO;
        }
    }
}