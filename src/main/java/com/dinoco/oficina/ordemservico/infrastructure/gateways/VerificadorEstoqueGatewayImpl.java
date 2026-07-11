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
     * Esse pode ser chamado em dois momentos:
     *
     * 1° Durante aprovação do orçamento, para verificar se temos estoque físico suficiente para todas as peças necessárias na OS.
     *
     * A reserva dos itens ainda não foi realizada, ela será feita de forma assíncrona pelo módulo de Estoque, que vai escutar o evento de
     * "Orçamento Aprovado" e vai fazer o registro da reserva.
     */

    @Override
    public boolean todasAsPecasEstaoDisponiveis(List<ItemOSProduto> itemOSProdutos) {
        // Se a lista de faltantes for vazia, significa que tem estoque para tudo!
        return verificarSeHaSaldoNoEstoque(itemOSProdutos).isEmpty();
    }

    public List<PecaPendenteDto> verificarSeHaSaldoNoEstoque(List<ItemOSProduto> itemOSProdutos) {
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

    /**
     * 2° Verificação de estoque, caso tenha peças faltando, para informar ao usuário quais peças estão em falta e qual a quantidade que falta comprar.
     * Se a quantidade real for igual a quantidade reservada, está tudo certo.
     */
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

        String sql = "SELECT produto_id, quantidade_real, quantidade_reservada FROM saldo_estoque WHERE produto_id IN (:ids)";
        MapSqlParameterSource params = new MapSqlParameterSource("ids", itensRequisitadosMap.keySet());

        Map<Long, Map<String, BigDecimal>> saldosBancoMap = jdbcTemplate.query(
                sql,
                params,
                rs -> {
                    Map<Long, Map<String, BigDecimal>> map = new HashMap<>();
                    while (rs.next()) {
                        Map<String, BigDecimal> saldos = new HashMap<>();
                        saldos.put("real", rs.getBigDecimal("quantidade_real"));
                        saldos.put("reservada", rs.getBigDecimal("quantidade_reservada"));
                        map.put(rs.getLong("produto_id"), saldos);
                    }
                    return map;
                }
        );

        List<PecaPendenteDto> pecasFaltantes = new ArrayList<>();

        for (Map.Entry<Long, BigDecimal> entry : itensRequisitadosMap.entrySet()) {
            Long produtoId = entry.getKey();
            BigDecimal quantidadeNecessariaNaOs = entry.getValue();

            Map<String, BigDecimal> saldos = saldosBancoMap.getOrDefault(produtoId, Map.of("real", BigDecimal.ZERO, "reservada", BigDecimal.ZERO));
            BigDecimal qtdReal = saldos.get("real");
            BigDecimal qtdReservadaTotal = saldos.get("reservada");

            BigDecimal saldoLivreGlobal = qtdReal.subtract(qtdReservadaTotal);
            BigDecimal saldoDisponivelParaEstaOs = saldoLivreGlobal.add(quantidadeNecessariaNaOs);

            if (saldoDisponivelParaEstaOs.compareTo(quantidadeNecessariaNaOs) < 0) {
                BigDecimal faltante = quantidadeNecessariaNaOs.subtract(saldoDisponivelParaEstaOs);
                pecasFaltantes.add(new PecaPendenteDto(produtoId, faltante));
            }
        }

        return pecasFaltantes;
    }
}