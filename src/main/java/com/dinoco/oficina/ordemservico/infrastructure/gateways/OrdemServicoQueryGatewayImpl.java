package com.dinoco.oficina.ordemservico.infrastructure.gateways;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoQueryGateway;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarporid.BuscarOSPorIdOuput;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarpornumero.BuscarOSPorCodigoRastreioOuput;
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
    public Optional<BuscarOSPorCodigoRastreioOuput> buscarPorCodigoRastreio(String codigoRastreio) {
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
        """;

        MapSqlParameterSource params = new MapSqlParameterSource("codigoRastreio", codigoRastreio);

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
