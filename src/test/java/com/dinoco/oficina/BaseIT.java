package com.dinoco.oficina;

import com.dinoco.oficina.infra.security.TokenService;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public abstract class BaseIT {

    @LocalServerPort
    private int port;

    // Sendo estático, o Spring Boot Testcontainers garante que este container
    // seja inicializado apenas UMA vez para todas as classes de teste que herdarem de BaseIT.
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private TokenService jwtService;

    @BeforeEach
    void setupGlobal() {
        // Configura a porta em que o Tomcat de teste subiu
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        String token = obterTokenValido();

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .setContentType(ContentType.JSON)
                .build();
    }

    private String obterTokenValido() {
        return jwtService.gerarToken("admin");
    }
}