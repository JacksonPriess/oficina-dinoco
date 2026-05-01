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
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIT {

    @LocalServerPort
    private int port;

    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    static {
        postgres.start();
    }

    @Autowired
    private TokenService jwtService;

    @BeforeEach
    void setupGlobal() {
        RestAssured.port = port;// Configura a porta em que o Tomcat de teste subiu
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