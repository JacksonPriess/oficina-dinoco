package com.dinoco.oficina.ordemservico.infrastructure;

import com.dinoco.oficina.metrica.infrastructure.gateways.MetricaQueryGatewayImpl;
import com.dinoco.oficina.ordemservico.infrastructure.gateways.CatalogoServicoGatewayImpl;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"spring.profiles.active=catalogo-servico-test", "spring.jpa.show-sql=false"})
@EnabledIfEnvironmentVariable(named = "NUCLEO_CATALOGO_SERVICO_INTEGRATION_TESTS", matches = "true")
class CatalogoServicoPostgresTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void agregaItemComIdExternoEUsaMetadadosDoCatalogoIncluindoServicoDesativado() throws Exception {
        assertThat(jdbc.queryForObject("SELECT current_database()", String.class)).endsWith("_test");
        var codigo = UUID.randomUUID().toString();
        var cliente = jdbc.queryForObject(
                "INSERT INTO cliente(tipo_pessoa,documento,nome) VALUES ('F',?,'Teste') RETURNING id",
                Long.class, codigo.substring(0, 8));
        var veiculo = jdbc.queryForObject(
                "INSERT INTO veiculo(placa,marca,modelo) VALUES (?,'Teste','Teste') RETURNING id",
                Long.class, codigo.substring(0, 8));
        var os = jdbc.queryForObject("""
                INSERT INTO ordem_servico(codigo_rastreio,cliente_id,veiculo_id,status,quilometragem_entrada,reclamacao_cliente)
                VALUES (?,?,?,'FINALIZADA',1,'Teste') RETURNING id
                """, Long.class, codigo, cliente, veiculo);
        long servicoExterno = 999999L;
        jdbc.update("""
                INSERT INTO item_os_servico(os_id,servico_id,valor_cobrado,status_item,data_inicio,data_fim)
                VALUES (?,?,100,'CONCLUIDO','2099-01-01 10:00:00','2099-01-01 10:20:00')
                """, os, servicoExterno);

        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/servicos", exchange -> {
            try (exchange) {
                if (!"servico-key".equals(exchange.getRequestHeaders().getFirst("X-API-Key"))
                        || !("ids=" + servicoExterno).equals(exchange.getRequestURI().getQuery())) {
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }
                var body = """
                        [{"id":999999,"descricao":"Descrição atual","precoPadrao":120,
                          "tempoEstimadoMinutos":30,"ativo":false}]
                        """.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, body.length);
                exchange.getResponseBody().write(body);
            }
        });
        server.start();
        try {
            var catalogo = new CatalogoServicoGatewayImpl(RestClient.builder(),
                    "http://127.0.0.1:" + server.getAddress().getPort(), "servico-key");
            var metricas = new MetricaQueryGatewayImpl(new NamedParameterJdbcTemplate(jdbc), catalogo);
            var dia = LocalDate.of(2099, 1, 1);
            assertThat(metricas.buscarDadosMetricas(dia, dia)).singleElement().satisfies(item -> {
                assertThat(item.servicoId()).isEqualTo(servicoExterno);
                assertThat(item.descricao()).isEqualTo("Descrição atual");
                assertThat(item.tempoPadraoMinutos()).isEqualTo(30);
                assertThat(item.quantidade()).isEqualTo(1L);
                assertThat(item.mediaMinutos()).isEqualByComparingTo("20");
            });
            assertThat(jdbc.queryForObject("SELECT valor_cobrado FROM item_os_servico WHERE os_id=?",
                    BigDecimal.class, os)).isEqualByComparingTo("100");
        } finally {
            server.stop(0);
            jdbc.update("DELETE FROM ordem_servico WHERE id=?", os);
            jdbc.update("DELETE FROM veiculo WHERE id=?", veiculo);
            jdbc.update("DELETE FROM cliente WHERE id=?", cliente);
        }
    }
}
