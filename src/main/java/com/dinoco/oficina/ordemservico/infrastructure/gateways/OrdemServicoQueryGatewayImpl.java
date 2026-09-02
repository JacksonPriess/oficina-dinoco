package com.dinoco.oficina.ordemservico.infrastructure.gateways;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoQueryGateway;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarporid.BuscarOSPorIdOuput;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarpornumero.BuscarOSPorCodigoRastreioOuput;
import com.dinoco.oficina.ordemservico.application.usecases.queries.listarfilatrabalho.ListarFilaTrabalhoDetalhesOutput;
import com.dinoco.oficina.ordemservico.infrastructure.persistence.OrdemServicoEntity;
import com.dinoco.oficina.ordemservico.infrastructure.persistence.OrdemServicoJpaRepository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OrdemServicoQueryGatewayImpl implements OrdemServicoQueryGateway {

    private final OrdemServicoJpaRepository jpaRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public OrdemServicoQueryGatewayImpl(OrdemServicoJpaRepository jpaRepository,
                                        NamedParameterJdbcTemplate jdbcTemplate) {
        this.jpaRepository = jpaRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<BuscarOSPorIdOuput> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::mapearParaOutput);
    }

    @Override
    public Optional<BuscarOSPorCodigoRastreioOuput> buscarPorCodigoRastreio(String codigoRastreio, Long clienteId) {
        String sql = """
            SELECT 
                os.id, 
                os.codigo_rastreio, 
                os.cliente_id, 
                c.nome AS nome_cliente,
                os.veiculo_id, 
                v.placa AS placa_veiculo, 
                os.reclamacao_cliente,
                os.quilometragem_entrada, 
                os.laudo_tecnico, 
                os.valor_total_servicos,
                os.valor_total_produtos, 
                os.valor_desconto, 
                os.valor_total_os, 
                os.status
            FROM ordem_servico os
            INNER JOIN cliente c ON os.cliente_id = c.id
            INNER JOIN veiculo v ON os.veiculo_id = v.id
            WHERE os.codigo_rastreio = :codigoRastreio
            AND os.cliente_id = :clienteId
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                        .addValue("codigoRastreio", codigoRastreio)
                        .addValue("clienteId", clienteId);

        List<BuscarOSPorCodigoRastreioOuput> resultado = jdbcTemplate.query(
                sql,
                params,
                (rs, rowNum) -> new BuscarOSPorCodigoRastreioOuput(
                        rs.getLong("id"),
                        rs.getString("codigo_rastreio"),
                        rs.getLong("cliente_id"),
                        rs.getString("nome_cliente"),
                        rs.getLong("veiculo_id"),
                        rs.getString("placa_veiculo"),
                        rs.getString("reclamacao_cliente"),
                        rs.getInt("quilometragem_entrada"),
                        rs.getString("laudo_tecnico"),
                        rs.getBigDecimal("valor_total_servicos"),
                        rs.getBigDecimal("valor_total_produtos"),
                        rs.getBigDecimal("valor_desconto"),
                        rs.getBigDecimal("valor_total_os"),
                        rs.getString("status")
                )
        );

        // Como o código de rastreio é único, a lista terá 0 ou 1 elemento.
        return resultado.stream().findFirst();
    }

    @Override
    public List<ListarFilaTrabalhoDetalhesOutput> listarFilaDeTrabalho() {
        String sql = """
            SELECT 
                os.id, 
                os.codigo_rastreio, 
                os.cliente_id, 
                os.veiculo_id, 
                os.status,
                os.data_entrada,
                os.valor_total_os
            FROM ordem_servico os
            WHERE os.status NOT IN ('FINALIZADA', 'ENTREGUE', 'REPROVADA' )
            ORDER BY 
                CASE os.status
                    WHEN 'EM_EXECUCAO' THEN 1
                    WHEN 'AGUARDANDO_APROVACAO' THEN 2
                    WHEN 'EM_DIAGNOSTICO' THEN 3
                    WHEN 'RECEBIDA' THEN 4
                    ELSE 5 -- Cobre status como AGUARDANDO_ORCAMENTO, AGUARDANDO_FORNECEDOR, AGUARDANDO_EXECUCAO
                END ASC,
                os.data_entrada ASC
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new ListarFilaTrabalhoDetalhesOutput(
                        rs.getLong("id"),
                        rs.getString("codigo_rastreio"),
                        rs.getLong("cliente_id"),
                        rs.getLong("veiculo_id"),
                        rs.getString("status"),
                        rs.getTimestamp("data_entrada") != null ? rs.getTimestamp("data_entrada").toLocalDateTime() : null,
                        rs.getBigDecimal("valor_total_os")
                )
        );
    }


    private BuscarOSPorIdOuput mapearParaOutput(OrdemServicoEntity entity) {
        return new BuscarOSPorIdOuput(
                entity.getId(),
                entity.getCodigoRastreio(),
                entity.getClienteId(),
                entity.getVeiculoId(),
                entity.getReclamacaoCliente(),
                entity.getQuilometragemEntrada(),
                entity.getLaudoTecnico(),
                entity.getValorTotalServicos(),
                entity.getValorTotalProdutos(),
                entity.getValorDesconto(),
                entity.getValorTotalOS(),
                entity.getStatus().toString()
        );
    }
}
