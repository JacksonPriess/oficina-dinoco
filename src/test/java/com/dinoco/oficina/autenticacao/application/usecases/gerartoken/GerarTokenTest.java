package com.dinoco.oficina.autenticacao.application.usecases.gerartoken;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;

import java.time.Instant;

public class GerarTokenTest {

    @Test
    void gerarTokensParaTesteLocal() {

        String secret =
                "minha-chave-secreta-de-desenvolvimento-local-1234567890";

        Algorithm algorithm = Algorithm.HMAC256(secret);

        String tokenCliente1 = JWT.create()
                .withIssuer("oficina-api")
                .withSubject("1")
                .withClaim("tipo", "CLIENTE")
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusSeconds(7200))
                .sign(algorithm);

        String tokenCliente11 = JWT.create()
                .withIssuer("oficina-api")
                .withSubject("11")
                .withClaim("tipo", "CLIENTE")
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusSeconds(7200))
                .sign(algorithm);

        System.out.println("CLIENTE 1: " + tokenCliente1); //gerando token do cliente 1
        System.out.println("CLIENTE 11: " + tokenCliente11); //gerando token do cliente 11
    }

}
