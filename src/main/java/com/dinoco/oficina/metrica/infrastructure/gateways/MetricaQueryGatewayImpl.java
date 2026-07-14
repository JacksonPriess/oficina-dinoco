package com.dinoco.oficina.metrica.infrastructure.gateways;

import com.dinoco.oficina.metrica.application.gateways.MetricaQueryGateway;
import com.dinoco.oficina.metrica.application.gateways.MetricaServicoData;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class MetricaQueryGatewayImpl implements MetricaQueryGateway {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public MetricaQueryGatewayImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MetricaServicoData> buscarDadosMetricas(LocalDate inicio, LocalDate fim) {
        String sql = """
            SELECT 
                s.id AS servicoId,
                s.descricao AS descricao,
                COUNT(i.id) AS quantidade,
                AVG(EXTRACT(EPOCH FROM (i.data_fim - i.data_inicio)) / 60) AS mediaMinutos,
                s.tempo_estimado_minutos AS tempoPadraoMinutos
            FROM item_os_servico i
            JOIN servico s ON s.id = i.servico_id
            WHERE i.status_item = 'CONCLUIDO'
              AND (CAST(:inicio AS timestamp) IS NULL OR i.data_fim >= CAST(:inicio AS timestamp))
              AND (CAST(:fim AS timestamp) IS NULL OR i.data_fim <= CAST(:fim AS timestamp))
            GROUP BY s.id, s.descricao, s.tempo_estimado_minutos
            """;

        LocalDateTime dataInicio = (inicio != null) ? inicio.atStartOfDay() : null;
        LocalDateTime dataFim = (fim != null) ? fim.atTime(LocalTime.MAX) : null;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("inicio", dataInicio)
                .addValue("fim", dataFim);

        // O RowMapper agora apenas descarrega o ResultSet no modelo de dados
        return jdbcTemplate.query(sql, params, (rs, rowNum) -> new MetricaServicoData(
                rs.getLong("servicoId"),
                rs.getString("descricao"),
                rs.getLong("quantidade"),
                rs.getBigDecimal("mediaMinutos"),
                rs.getInt("tempoPadraoMinutos")
        ));
    }
}