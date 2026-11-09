package com.dinoco.oficina.metrica.infrastructure.gateways;

import com.dinoco.oficina.metrica.application.gateways.MetricaQueryGateway;
import com.dinoco.oficina.metrica.application.gateways.MetricaServicoData;
import com.dinoco.oficina.exception.CatalogoServicoIntegracaoException;
import com.dinoco.oficina.ordemservico.application.gateways.CatalogoServicoGateway;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class MetricaQueryGatewayImpl implements MetricaQueryGateway {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final CatalogoServicoGateway catalogoServico;

    public MetricaQueryGatewayImpl(NamedParameterJdbcTemplate jdbcTemplate, CatalogoServicoGateway catalogoServico) {
        this.jdbcTemplate = jdbcTemplate;
        this.catalogoServico = catalogoServico;
    }

    @Override
    public List<MetricaServicoData> buscarDadosMetricas(LocalDate inicio, LocalDate fim) {
        String sql = """
            SELECT i.servico_id AS servicoId,
                   COUNT(i.id) AS quantidade,
                   AVG(EXTRACT(EPOCH FROM (i.data_fim - i.data_inicio)) / 60) AS mediaMinutos
            FROM item_os_servico i
            WHERE i.status_item = 'CONCLUIDO'
              AND (CAST(:inicio AS timestamp) IS NULL OR i.data_fim >= CAST(:inicio AS timestamp))
              AND (CAST(:fim AS timestamp) IS NULL OR i.data_fim <= CAST(:fim AS timestamp))
            GROUP BY i.servico_id
            """;

        LocalDateTime dataInicio = (inicio != null) ? inicio.atStartOfDay() : null;
        LocalDateTime dataFim = (fim != null) ? fim.atTime(LocalTime.MAX) : null;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("inicio", dataInicio)
                .addValue("fim", dataFim);

        var agregados = jdbcTemplate.query(sql, params, (rs, rowNum) -> new Agregado(
                rs.getLong("servicoId"), rs.getLong("quantidade"), rs.getBigDecimal("mediaMinutos")));
        if (agregados.isEmpty()) return List.of();

        var metadados = catalogoServico.buscarDetalhes(agregados.stream().map(Agregado::servicoId).toList());
        return agregados.stream().map(agregado -> {
            var servico = metadados.get(agregado.servicoId());
            if (servico == null) {
                throw new CatalogoServicoIntegracaoException("Serviço histórico ausente no católogo: " + agregado.servicoId());
            }
            return new MetricaServicoData(agregado.servicoId(), servico.descricao(), agregado.quantidade(),
                    agregado.mediaMinutos(), servico.tempoEstimadoMinutos());
        }).toList();
    }

    private record Agregado(Long servicoId, Long quantidade, BigDecimal mediaMinutos) {}
}
