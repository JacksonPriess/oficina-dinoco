package com.dinoco.oficina.ordemservico.infrastructure.gateways;

import com.dinoco.oficina.exception.*;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.client.RestClient;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import static org.assertj.core.api.Assertions.*;

class CatalogosHttpGatewayTest {
    private HttpServer server;
    private String url;
    private int status = 200;
    private String body = "{}";
    private final List<String> caminhos = new CopyOnWriteArrayList<>();
    private final List<String> chaves = new CopyOnWriteArrayList<>();
    @BeforeEach void iniciar() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            caminhos.add(exchange.getRequestURI().toString());
            chaves.add(exchange.getRequestHeaders().getFirst("X-API-Key"));
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, bytes.length);
            exchange.getResponseBody().write(bytes); exchange.close();
        });
        server.start(); url = "http://127.0.0.1:" + server.getAddress().getPort();
    }
    @AfterEach void fechar() { server.stop(0); }
    private CatalogoProdutoGatewayImpl produtos() { return new CatalogoProdutoGatewayImpl(RestClient.builder(), url, "chave-teste"); }
    private CatalogoServicoGatewayImpl servicos() { return new CatalogoServicoGatewayImpl(RestClient.builder(), url, "chave-teste"); }

    @Test void consultarPrecosEnviandoCredencialEId() {
        body = "{\"id\":7,\"precoVenda\":32.50,\"precoPadrao\":45.70,\"extra\":true}";
        assertThat(produtos().buscarPrecoVendaAtual(7L)).isEqualByComparingTo("32.50");
        assertThat(servicos().buscarPrecoPadrao(7L)).hasValueSatisfying(p -> assertThat(p).isEqualByComparingTo("45.70"));
        assertThat(caminhos).containsExactly("/api/produtos/7", "/api/servicos/7");
        assertThat(chaves).containsOnly("chave-teste");
    }
    @Test void ausenciaNoCatalogoNaoEIndisponibilidade() {
        status = 404;
        assertThat(produtos().buscarPrecoVendaAtual(7L)).isNull();
        assertThat(servicos().buscarPrecoPadrao(7L)).isEmpty();
    }
    @ParameterizedTest @ValueSource(ints = {401, 403, 500, 503})
    void traduzirFalhaHttp(int codigo) {
        status = codigo;
        assertThatThrownBy(() -> produtos().buscarPrecoVendaAtual(7L)).isInstanceOf(CatalogoProdutoIntegracaoException.class).hasMessageContaining("HTTP " + codigo);
        assertThatThrownBy(() -> servicos().buscarPrecoPadrao(7L)).isInstanceOf(CatalogoServicoIntegracaoException.class).hasMessageContaining("HTTP " + codigo);
    }
    @ParameterizedTest
    @ValueSource(strings = {"null", "{}", "{\"id\":8,\"precoVenda\":20,\"precoPadrao\":20}", "{\"id\":7,\"precoVenda\":-1,\"precoPadrao\":-1}", "nao-json"})
    void rejeitarPrecoInvalido(String json) {
        body = json;
        assertThatThrownBy(() -> produtos().buscarPrecoVendaAtual(7L)).isInstanceOf(CatalogoProdutoIntegracaoException.class);
        assertThatThrownBy(() -> servicos().buscarPrecoPadrao(7L)).isInstanceOf(CatalogoServicoIntegracaoException.class);
    }
    @Test void consultarDetalhesSemRepetirIds() {
        body = "[{\"id\":7,\"descricao\":\"Troca\",\"precoPadrao\":45.70,\"tempoEstimadoMinutos\":30}]";
        var result = servicos().buscarDetalhes(List.of(7L, 7L));
        assertThat(result).hasSize(1);
        assertThat(result.get(7L).descricao()).isEqualTo("Troca");
        assertThat(result.get(7L).tempoEstimadoMinutos()).isEqualTo(30);
        assertThat(caminhos).containsExactly("/api/servicos?ids=7");
    }
    @Test void dividirConsultaEmLotesDeCem() {
        body = "[]";
        assertThat(servicos().buscarDetalhes(java.util.stream.LongStream.rangeClosed(1, 101).boxed().toList())).isEmpty();
        assertThat(caminhos).hasSize(2);
        assertThat(caminhos.get(0).split("ids=")[1].split(",")).hasSize(100);
        assertThat(caminhos.get(1)).isEqualTo("/api/servicos?ids=101");
    }
    @ParameterizedTest @ValueSource(strings = {"null", "[null]", "[{}]", "[{\"id\":8}]", "[{\"id\":7,\"descricao\":\"Troca\"}]"})
    void rejeitarLoteInconsistente(String json) {
        body = json;
        assertThatThrownBy(() -> servicos().buscarDetalhes(List.of(7L))).isInstanceOf(CatalogoServicoIntegracaoException.class);
    }
    @Test void listaVaziaNaoChamaRedeEConexaoRecusadaTemExcecaoDeIntegracao() {
        assertThat(servicos().buscarDetalhes(List.of())).isEmpty();
        assertThat(caminhos).isEmpty(); server.stop(0);
        assertThatThrownBy(() -> produtos().buscarPrecoVendaAtual(7L)).isInstanceOf(CatalogoProdutoIntegracaoException.class);
        assertThatThrownBy(() -> servicos().buscarDetalhes(List.of(7L))).isInstanceOf(CatalogoServicoIntegracaoException.class);
    }
}
