package com.dinoco.oficina.metrica;

import com.dinoco.oficina.metrica.infrastructure.gateways.MetricaQueryGatewayImpl;
import com.dinoco.oficina.metrica.infrastructure.config.MetricaConfig;
import com.dinoco.oficina.metrica.infrastructure.web.MetricaSpringWebController;
import com.dinoco.oficina.metrica.infrastructure.web.mapper.MetricaWebMapper;
import com.dinoco.oficina.metrica.domain.MetricaServico;
import com.dinoco.oficina.ordemservico.application.gateways.CatalogoServicoGateway;
import com.dinoco.oficina.exception.CatalogoServicoIntegracaoException;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.sql.ResultSet;
import java.math.BigDecimal;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MetricasTest {
    @Test void calcularDesvioComArredondamentoETratarPadraoZero() {
        var metrica = new MetricaServico(1L, "Troca", 2L, new BigDecimal("45.555"), 30);
        assertThat(metrica.mediaMinutos()).isEqualByComparingTo("45.56");
        assertThat(metrica.calcularDesvioAbsoluto()).isEqualByComparingTo("15.56");
        assertThat(metrica.calcularPercentualDiferenca()).isEqualByComparingTo("51.87");
        var vazia = new MetricaServico(1L, "Troca", 0L, null, 0);
        assertThat(vazia.mediaMinutos()).isZero();
        assertThat(vazia.calcularPercentualDiferenca()).isZero();
    }

    @Test void combinarAgregacaoComCatalogoEExporRelatorio() throws Exception {
        var jdbc = mock(NamedParameterJdbcTemplate.class);
        var catalogo = mock(CatalogoServicoGateway.class);
        var rs = mock(ResultSet.class);
        when(rs.getLong("servicoId")).thenReturn(7L);
        when(rs.getLong("quantidade")).thenReturn(2L);
        when(rs.getBigDecimal("mediaMinutos")).thenReturn(new BigDecimal("45"));
        when(jdbc.query(anyString(), any(SqlParameterSource.class), any(RowMapper.class)))
                .thenAnswer(i -> List.of(i.<RowMapper<?>>getArgument(2).mapRow(rs, 0)));
        when(catalogo.buscarDetalhes(List.of(7L))).thenReturn(Map.of(7L,
                new CatalogoServicoGateway.ServicoCatalogo(7L, "Troca de óleo", BigDecimal.TEN, 30)));
        var gateway = new MetricaQueryGatewayImpl(jdbc, catalogo);
        var config = new MetricaConfig();
        var mvc = MockMvcBuilders.standaloneSetup(new MetricaSpringWebController(
                config.metricaControllerClean(config.buscarMediaExecucaoServicosUseCase(gateway)), Mappers.getMapper(MetricaWebMapper.class))).build();
        mvc.perform(get("/api/metricas/tempo-execucao").param("dataInicio", "2026-01-01").param("dataFinal", "2026-01-31"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.detalhes[0].mediaMinutos").value(45))
                .andExpect(jsonPath("$.detalhes[0].percentualDiferenca").value(50));
        when(catalogo.buscarDetalhes(anyList())).thenReturn(Map.of());
        assertThatThrownBy(() -> gateway.buscarDadosMetricas(null, null)).isInstanceOf(CatalogoServicoIntegracaoException.class);
        when(jdbc.query(anyString(), any(SqlParameterSource.class), any(RowMapper.class))).thenReturn(List.of());
        assertThat(gateway.buscarDadosMetricas(null, null)).isEmpty();
    }
}
