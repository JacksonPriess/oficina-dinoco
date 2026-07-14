package com.dinoco.oficina.ordemservico.infrastructure.gateways;

import com.dinoco.oficina.ordemservico.application.gateways.CatalogoServicoGateway;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Optional;

@Component
public class CatalogoServicoGatewayImpl implements CatalogoServicoGateway {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CatalogoServicoGatewayImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<BigDecimal> buscarPrecoPadrao(Long servicoId) {
        String sql = """
            SELECT preco_padrao
            FROM servico
            WHERE id = :servicoId
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("servicoId", servicoId);

        BigDecimal precoPadrao = jdbcTemplate.queryForObject(
                sql,
                params,
                BigDecimal.class
        );

        return Optional.ofNullable(precoPadrao);
    }
}