package com.dinoco.oficina.ordemservico.infrastructure.gateways;

import com.dinoco.oficina.ordemservico.application.gateways.VerificadorEstoqueGateway;
import com.dinoco.oficina.ordemservico.domain.models.ItemOSProduto;
import com.dinoco.oficina.ordemservico.infrastructure.web.dto.PecaPendenteDto;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class VerificadorEstoqueGatewayImpl implements VerificadorEstoqueGateway {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public VerificadorEstoqueGatewayImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Esse gateway será chamado durante a aprovação do orçamento,
     * para verificar se temos estoque suficiente para todas as peças necessárias.
     *
     * Mas a reserva dos itens ainda não foi realizada, ela será feita de
     * forma assíncrona pelo módulo de Estoque, que vai escutar o evento de
     * "Orçamento Aprovado" e vai fazer o registro da reserva.
     *
     */
    @Override
    public boolean todasAsPecasEstaoDisponiveis(List<ItemOSProduto> itemOSProdutos) {
        // Se a lista de faltantes for vazia, significa que tem estoque para tudo!
        return buscarPecasComEstoqueInsuficiente(itemOSProdutos).isEmpty();
    }

    @Override
    public List<PecaPendenteDto> buscarPecasComEstoqueInsuficiente(List<ItemOSProduto> itemOSProdutos) {
        if (itemOSProdutos == null || itemOSProdutos.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, BigDecimal> itensRequisitadosMap = itemOSProdutos.stream()
                .collect(Collectors.toMap(
                        ItemOSProduto::getProdutoId,
                        ItemOSProduto::getQuantidade
                ));

        String sql = "SELECT produto_id, (quantidade_real - quantidade_reservada) AS saldo_disponivel " +
                "FROM saldo_estoque WHERE produto_id IN (:ids)";
        MapSqlParameterSource params = new MapSqlParameterSource("ids", itensRequisitadosMap.keySet());

        Map<Long, BigDecimal> saldosBancoMap = jdbcTemplate.query(
                sql,
                params,
                rs -> {
                    Map<Long, BigDecimal> map = new HashMap<>();
                    while (rs.next()) {
                        map.put(rs.getLong("produto_id"), rs.getBigDecimal("saldo_disponivel"));
                    }
                    return map;
                }
        );

        List<PecaPendenteDto> pecasFaltantes = new ArrayList<>();

        for (Map.Entry<Long, BigDecimal> entry : itensRequisitadosMap.entrySet()) {
            Long produtoId = entry.getKey();
            BigDecimal quantidadeNecessaria = entry.getValue();
            BigDecimal saldoDisponivel = saldosBancoMap.getOrDefault(produtoId, BigDecimal.ZERO);
            // Se o saldo for menor que o necessário, calcula o que falta comprar
            if (saldoDisponivel.compareTo(quantidadeNecessaria) < 0) {
                BigDecimal faltante = quantidadeNecessaria.subtract(saldoDisponivel);
                pecasFaltantes.add(new PecaPendenteDto(produtoId, faltante));
            }
        }

        return pecasFaltantes;
    }
}