package com.dinoco.oficina.controller;

import com.dinoco.oficina.BaseIT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;

@Sql(scripts = "/scripts/preparar_cenarios_os.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class OrdemServicoControllerIT extends BaseIT {

    @Test
    @DisplayName("01 - Deve abrir uma ordem de serviço com sucesso (Status 201)")
    void deveAbrirOsComSucesso() {

        String payload = """
                {
                    "clienteId": 999,
                    "veiculoId": 888,
                    "quilometragemEntrada": 45000,
                    "reclamacaoCliente": "Luz da injeção acesa no painel"
                }
                """;

        given()
                .body(payload)
        .when()
                .post("/api/ordens-servico")
        .then()
                .statusCode(201)
                .header("Location", containsString("/api/ordens-servico/"))
                .body("clienteId", equalTo(999))
                .body("veiculoId", equalTo(888))
                .body("quilometragemEntrada", equalTo(45000))
                .body("id", notNullValue())
                .body("codigoRastreio", notNullValue())
                .body("status", equalTo("RECEBIDA"));
    }

    @Test
    @DisplayName("Deve buscar uma OS por ID incluindo itens de serviço e produto")
    void deveBuscarOsPorIdComItens() {
        given()
                .pathParam("osId", 102)
        .when()
                .get("/api/ordens-servico/{osId}")
        .then()
                .statusCode(200)
                .body("itensServico", hasSize(1))
                .body("itensServico[0].servicoId", equalTo(666))
                .body("itensServico[0].valorCobrado", equalTo(120.0f))
                .body("itensProduto", hasSize(1))
                .body("itensProduto[0].produtoId", equalTo(777))
                .body("itensProduto[0].quantidade", equalTo(1.0f))
                .body("itensProduto[0].valorUnitarioVenda", equalTo(150.0f))
                .body("itensProduto[0].valorTotal", equalTo(150.0f));
    }

    @Test
    @DisplayName("Deve buscar uma OS por ID e não encontrar itens de serviço e produto")
    void deveBuscarOsPorIdSemItens() {
        given()
                .pathParam("osId", 100)
                .when()
                .get("/api/ordens-servico/{osId}")
                .then()
                .statusCode(200)
                .body("itensServico", hasSize(0))
                .body("itensProduto", hasSize(0))
                .body("dataEntrada", notNullValue());
    }

    @Test
    @DisplayName("02 - Deve iniciar o diagnóstico de uma OS que está com status RECEBIDA (Cenário A - OS 100)")
    void deveIniciarDiagnostico() {
        given()
                .pathParam("id", 100)
        .when()
                .post("/api/ordens-servico/{id}/iniciar-diagnostico")
        .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("03 - Deve concluir o diagnóstico informando o laudo (Cenário B - OS 101)")
    void deveConcluirDiagnostico() {
        String payloadLaudo = """
                {
                    "laudo": "Verificado desgaste nas pastilhas dianteiras. Necessária a substituição."
                }
                """;

        given()
                .pathParam("id", 101)
                .body(payloadLaudo)
        .when()
                .post("/api/ordens-servico/{id}/concluir-diagnostico")
        .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("04 - Deve enviar orçamento e retornar o DTO com link do WhatsApp (Cenário C - OS 102)")
    void deveEnviarOrcamento() {
        given()
                .pathParam("id", 102)
        .when()
                .post("/api/ordens-servico/{id}/enviar-orcamento")
        .then()
                .statusCode(200)
                .body("linkWhatsApp", notNullValue())
                .body("linkWhatsApp", containsString("wa.me/5547999990001"));
    }

    @Test
    @DisplayName("05 - Deve reprovar o orçamento (Cenário D - OS 103)")
    void deveReprovarOrcamento() {
        given()
                .pathParam("id", 103)
        .when()
                .post("/api/ordens-servico/{id}/reprovar")
        .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("06 - Deve aprovar orçamento e reservar estoque (Cenário D - OS 103)")
    void deveAprovarOrcamento() {
        given()
                .pathParam("id", 103)
        .when()
                .post("/api/ordens-servico/{id}/aprovar")
        .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Erro 03 - Não deve concluir diagnóstico de uma OS sem itens de serviço (Cenário A - OS 100)")
    void naoDeveConcluirDiagnosticoDeOsSemServico() {
        // Tentamos usar a OS 100, que não tem itens lançados no banco
        given()
                .pathParam("id", 100)
                .body("{ \"laudo\": \"Laudo de teste\" }")
        .when()
                .post("/api/ordens-servico/{id}/concluir-diagnostico")
        .then()
                .statusCode(400);
    }

}