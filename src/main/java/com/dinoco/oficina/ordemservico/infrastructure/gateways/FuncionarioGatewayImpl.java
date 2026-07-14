package com.dinoco.oficina.ordemservico.infrastructure.gateways;

import com.dinoco.oficina.ordemservico.application.gateways.FuncionarioGateway;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class FuncionarioGatewayImpl implements FuncionarioGateway {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public FuncionarioGatewayImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean existeMecanicoAtivo(Long mecanicoId) {
        if (mecanicoId == null) {
            return false;
        }
        String sql = "SELECT COUNT(1) FROM funcionario WHERE id = :id AND ativo = true";
        MapSqlParameterSource params = new MapSqlParameterSource("id", mecanicoId);
        Integer count = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }
}
