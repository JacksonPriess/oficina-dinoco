package com.dinoco.oficina.ordemservico.infrastructure.gateways;

import com.dinoco.oficina.ordemservico.infrastructure.persistence.OrdemServicoJpaRepository;
import com.dinoco.oficina.ordemservico.domain.models.ItemOSProduto;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.*;
import java.sql.*;
import java.math.BigDecimal;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsultasJdbcTest {
    private final NamedParameterJdbcTemplate jdbc = mock(NamedParameterJdbcTemplate.class);

    @Test void rastreioMantemFiltroDoClienteEMapeiaResposta() throws Exception {
        var rs = mock(ResultSet.class);
        when(rs.getLong("id")).thenReturn(9L);
        when(rs.getLong("cliente_id")).thenReturn(11L);
        when(rs.getString("codigo_rastreio")).thenReturn("OS-TESTE");
        when(rs.getString("nome_cliente")).thenReturn("Ana");
        when(rs.getString("placa_veiculo")).thenReturn("ABC1D23");
        when(rs.getBigDecimal("valor_total_os")).thenReturn(new BigDecimal("250.50"));
        when(jdbc.query(anyString(), any(SqlParameterSource.class), any(RowMapper.class))).thenAnswer(i -> {
            assertThat(i.<String>getArgument(0)).contains("os.cliente_id = :clienteId");
            assertThat(i.<SqlParameterSource>getArgument(1).getValue("clienteId")).isEqualTo(11L);
            assertThat(i.<SqlParameterSource>getArgument(1).getValue("codigoRastreio")).isEqualTo("OS-TESTE");
            return List.of(i.<RowMapper<?>>getArgument(2).mapRow(rs, 0));
        });
        var gateway = new OrdemServicoQueryGatewayImpl(mock(OrdemServicoJpaRepository.class), jdbc);
        var output = gateway.buscarPorCodigoRastreio("OS-TESTE", 11L).orElseThrow();
        assertThat(output.id()).isEqualTo(9L);
        assertThat(output.nomeCliente()).isEqualTo("Ana");
        assertThat(output.placaVeiculo()).isEqualTo("ABC1D23");
        assertThat(output.valorTotalOS()).isEqualByComparingTo("250.50");
    }

    @Test void filaMapeiaDataETotalEExcluiEstadosFinais() throws Exception {
        var rs = mock(ResultSet.class);
        when(rs.getLong("id")).thenReturn(9L);
        when(rs.getString("status")).thenReturn("EM_EXECUCAO");
        var timestamp = Timestamp.valueOf("2026-01-02 10:00:00");
        when(rs.getTimestamp("data_entrada")).thenReturn(timestamp);
        when(rs.getBigDecimal("valor_total_os")).thenReturn(BigDecimal.TEN);
        when(jdbc.query(anyString(), any(RowMapper.class))).thenAnswer(i -> {
            assertThat(i.<String>getArgument(0)).contains("NOT IN ('FINALIZADA', 'ENTREGUE', 'REPROVADA' )", "ORDER BY");
            return List.of(i.<RowMapper<?>>getArgument(1).mapRow(rs, 0));
        });
        var output = new OrdemServicoQueryGatewayImpl(mock(OrdemServicoJpaRepository.class), jdbc).listarFilaDeTrabalho();
        assertThat(output).singleElement().satisfies(os -> {
            assertThat(os.id()).isEqualTo(9L);
            assertThat(os.dataEntrada()).isEqualTo(timestamp.toLocalDateTime());
            assertThat(os.status()).isEqualTo("EM_EXECUCAO");
        });
    }

    @Test void contatoUsaIdentificadorEFuncionarioAusenteNaoExiste() throws Exception {
        var rs = mock(ResultSet.class);
        when(rs.getString("nome")).thenReturn("Ana");
        when(rs.getString("telefone")).thenReturn("47999999999");
        when(jdbc.queryForObject(anyString(), any(SqlParameterSource.class), any(RowMapper.class))).thenAnswer(i -> {
            assertThat(i.<SqlParameterSource>getArgument(1).getValue("clienteId")).isEqualTo(3L);
            return i.<RowMapper<?>>getArgument(2).mapRow(rs, 0);
        });
        var contato = new ClienteContatoGatewayImpl(jdbc).buscarContato(3L);
        assertThat(contato.nome()).isEqualTo("Ana");
        assertThat(contato.telefone()).isEqualTo("47999999999");
        var funcionario = new FuncionarioGatewayImpl(jdbc);
        assertThat(funcionario.existeMecanicoAtivo(null)).isFalse();
        when(jdbc.queryForObject(anyString(), any(SqlParameterSource.class), eq(Integer.class))).thenReturn(null, 0, 1);
        assertThat(funcionario.existeMecanicoAtivo(3L)).isFalse();
        assertThat(funcionario.existeMecanicoAtivo(3L)).isFalse();
        assertThat(funcionario.existeMecanicoAtivo(3L)).isTrue();
    }

    @Test void disponibilidadeConsideraReservasEProdutosSemSaldo() throws Exception {
        var rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true, false);
        when(rs.getLong("produto_id")).thenReturn(7L);
        when(rs.getBigDecimal("saldo_disponivel")).thenReturn(BigDecimal.ONE);
        when(jdbc.query(anyString(), any(SqlParameterSource.class), any(ResultSetExtractor.class)))
                .thenAnswer(i -> i.<ResultSetExtractor<?>>getArgument(2).extractData(rs));
        var gateway = new VerificadorEstoqueGatewayImpl(jdbc);
        assertThat(gateway.todasAsPecasEstaoDisponiveis(null)).isTrue();
        assertThat(gateway.buscarPecasComEstoqueInsuficiente(List.of())).isEmpty();
        var itens = List.of(new ItemOSProduto(1L, 7L, new BigDecimal("3"), BigDecimal.TEN),
                new ItemOSProduto(2L, 8L, BigDecimal.ONE, BigDecimal.TEN));
        var faltantes = gateway.verificarSeHaSaldoNoEstoque(itens);
        assertThat(faltantes).hasSize(2);
        assertThat(faltantes).extracting("produtoId").containsExactlyInAnyOrder(7L, 8L);
        assertThat(faltantes).extracting("quantidadeFaltante").containsExactlyInAnyOrder(new BigDecimal("2"), BigDecimal.ONE);
        reset(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getLong("produto_id")).thenReturn(7L);
        when(rs.getBigDecimal("quantidade_real")).thenReturn(BigDecimal.ONE);
        when(rs.getBigDecimal("quantidade_reservada")).thenReturn(new BigDecimal("3"));
        assertThat(gateway.buscarPecasComEstoqueInsuficiente(itens)).singleElement()
                .satisfies(peca -> assertThat(peca.produtoId()).isEqualTo(7L));
    }
}
