package com.dinoco.oficina.bdd;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.config.HttpClientConfig;

import java.util.UUID;

import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cliente restrito ao teste; nunca imprime senhas, tokens ou corpos completos.
 */
final class BddApi {
    final String correlationId = UUID.randomUUID().toString();
    String token;

    static String env(String nome) {
        String value = System.getenv(nome);

        if (value == null || value.isBlank())
            throw new IllegalStateException("Configure " + nome + ". Consulte docs/bdd/BDD_README.md.");

        return value;
    }

    void validarConfiguracao() {
        for (String nome : new String[]{
                "BDD_NUCLEO_URL",
                "BDD_PRODUTO_URL",
                "BDD_SERVICO_URL",
                "BDD_FINANCEIRO_URL",
                "BDD_MOCK_URL",
                "BDD_LOGIN",
                "BDD_PASSWORD",
                "CATALOGO_API_KEY",
                "CATALOGO_SERVICO_API_KEY",
                "FINANCEIRO_API_KEY",
                "MOCK_ADMIN_KEY"})
            env(nome);
    }

    Response chamar(String api, String method, String path, Object body) {
        var request = RestAssured.given().baseUri(env("BDD_" + api + "_URL"))
                .contentType("application/json").header("X-Correlation-Id", correlationId)
                .config(RestAssured.config().httpClient(
                        HttpClientConfig.httpClientConfig().setParam("http.connection.timeout", 10000).setParam(
                                "http.socket.timeout", 10000)));
        switch (api) {
            case "NUCLEO" -> {
                if (token != null)
                    request.header("Authorization", "Bearer " + token);
            }
            case "PRODUTO" -> request.header("X-API-Key", env("CATALOGO_API_KEY"));
            case "SERVICO" -> request.header("X-API-Key", env("CATALOGO_SERVICO_API_KEY"));
            case "FINANCEIRO" -> request.header("X-API-Key", env("FINANCEIRO_API_KEY"));
            case "MOCK" -> request.header("X-Mock-Admin-Key", env("MOCK_ADMIN_KEY"));
            default -> throw new IllegalArgumentException("API desconhecida: " + api);
        }
        if (body != null)
            request.body(JsonMapper.builder().build().writeValueAsString(body));

        var response = request.request(method, path);

        assertThat(response.statusCode()).as("%s %s %s (correlationId=%s)", api, method, path, correlationId).isBetween(
                200, 299);

        return response;
    }
}
