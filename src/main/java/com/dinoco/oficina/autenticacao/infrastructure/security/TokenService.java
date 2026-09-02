package com.dinoco.oficina.autenticacao.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String gerarToken(String username) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("oficina-api")
                    .withSubject(username)
                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public Optional<TokenAutenticado> validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            var decodedJWT = JWT.require(algorithm)
                    .withIssuer("oficina-api")
                    .build()
                    .verify(token);

            return Optional.of(
                    new TokenAutenticado(
                            decodedJWT.getSubject(),
                            decodedJWT.getClaim("tipo").asString()
                    )
            );

        } catch (JWTVerificationException exception) {
            return Optional.empty();
        }
    }

    private Instant gerarDataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
