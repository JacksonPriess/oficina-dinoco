package com.dinoco.oficina.controller;

import com.dinoco.oficina.BaseIT;
import com.dinoco.oficina.util.builders.VeiculoRequestDtoBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.jdbc.Sql;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Sql(scripts = "/scripts/limpar_dados.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/scripts/limpar_veiculos.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class VeiculoControllerIT extends BaseIT {

    @ParameterizedTest(name = "Deve criar veículo com a placa válida: {0}")
    @ValueSource(strings = {
            "ABC1234", // Padrão Antigo
            "ABC1D23", // Padrão Mercosul (Letra no meio)
            "XYZ9A87"  // Padrão Mercosul alternativo
    })
    void deveCriarVeiculoComPlacasValidas(String placaValida) {
        var request = VeiculoRequestDtoBuilder.umRequest().comPlaca(placaValida).build();
        given()
                .body(request)
        .when()
                .post("/api/veiculos")
        .then()
                .statusCode(201)
                .header("Location", containsString("/api/veiculos/"))
                .body("id", notNullValue())
                .body("placa", equalTo(placaValida))
                .body("ativo", is(true));
    }

    @ParameterizedTest(name = "Deve rejeitar criação com a placa inválida: {0}")
    @ValueSource(strings = {
            "ABC-1234", // Contém hífen
            "abc1234",  // Letras minúsculas
            "ABCD123",  // 4 letras
            "AB12345",  // 2 letras
            "123ABCD",  // Começa com números
            "ABC12345", // Mais de 7 caracteres
            "ABC12"     // Menos de 7 caracteres
    })
    void deveRetornarErro400ParaPlacasInvalidas(String placaInvalida) {
        var request = VeiculoRequestDtoBuilder.umRequest().comPlaca(placaInvalida).build();
        given()
                .body(request)
        .when()
                .post("/api/veiculos")
        .then()
                // Retorna 400 Bad Request porque a anotação @Valid do Controller barrou
                .statusCode(400);
    }

    @Test
    @DisplayName("Deve buscar um veículo existente por ID")
    @Sql(scripts = "/scripts/inserir_veiculo_teste.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deveBuscarVeiculoPorId() {
        given()
                .pathParam("id", 100)
        .when()
                .get("/api/veiculos/{id}")
        .then()
                .statusCode(200)
                .body("id", equalTo(100))
                .body("placa", equalTo("KLA2024"))
                .body("marca", equalTo("Toyota"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar veículo com ID inexistente")
    void deveRetornar404ParaVeiculoInexistente() {
        given()
                .pathParam("id", 9999)
        .when()
                .get("/api/veiculos/{id}")
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Deve atualizar os dados do veículo com sucesso")
    @Sql(scripts = "/scripts/inserir_veiculo_teste.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deveAtualizarVeiculo() {
        var requestUpdate = VeiculoRequestDtoBuilder.umRequest().comCor("Branco").build();
        given()
                .pathParam("id", 100)
                .body(requestUpdate)
        .when()
                .put("/api/veiculos/{id}")
        .then()
                .statusCode(200)
                .body("cor", equalTo("Branco"));
    }

    @Test
    @DisplayName("Deve desativar o veículo com sucesso")
    @Sql(scripts = "/scripts/inserir_veiculo_teste.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deveDesativarVeiculo() {

        given()
                .pathParam("id", 100)
        .when()
                .delete("/api/veiculos/{id}")
        .then()
                .statusCode(204);

        given()
                .pathParam("id", 100)
        .when()
                .get("/api/veiculos/{id}")
        .then()
                .statusCode(200)
                .body("ativo", is(false));
    }
    
}