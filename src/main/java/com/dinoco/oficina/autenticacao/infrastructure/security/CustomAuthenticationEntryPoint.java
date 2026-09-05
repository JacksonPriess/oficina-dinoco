package com.dinoco.oficina.autenticacao.infrastructure.security;

import com.dinoco.oficina.exception.ErroPadraoDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErroPadraoDto erro = new ErroPadraoDto(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Não autenticado",
                List.of("É necessário informar um token válido para acessar este recurso.")
        );

        response.getWriter().write(objectMapper.writeValueAsString(erro));
    }
}