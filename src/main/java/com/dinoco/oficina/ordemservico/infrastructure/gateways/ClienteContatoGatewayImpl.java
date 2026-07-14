package com.dinoco.oficina.ordemservico.infrastructure.gateways;

import com.dinoco.oficina.ordemservico.application.gateways.ClienteContatoGateway;
import com.dinoco.oficina.ordemservico.application.gateways.ClienteContatoOutput;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ClienteContatoGatewayImpl implements ClienteContatoGateway {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ClienteContatoGatewayImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ClienteContatoOutput buscarContato(Long clienteId) {
        String sql = """
            SELECT nome, telefone
            FROM cliente
            WHERE id = :clienteId
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("clienteId", clienteId);

        return jdbcTemplate.queryForObject(
                sql,
                params,
                (rs, rowNum) -> new ClienteContatoOutput(
                        rs.getString("nome"),
                        rs.getString("telefone")
                )
        );
    }
}
