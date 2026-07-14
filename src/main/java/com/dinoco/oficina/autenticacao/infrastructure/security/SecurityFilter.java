package com.dinoco.oficina.autenticacao.infrastructure.security;

import com.dinoco.oficina.autenticacao.infrastructure.persistence.UsuarioEntity;
import com.dinoco.oficina.exception.ErroPadraoDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    public SecurityFilter(TokenService tokenService, UserDetailsService userDetailsService, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = recuperarToken(request);

        if (token != null) {
            var subject = tokenService.validarToken(token);
            if (!subject.isEmpty()) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
                if (userDetails instanceof UsuarioEntity usuario) {
                    String uri = request.getRequestURI();
                    boolean isRotaTrocaSenha = uri.equals("/api/auth/trocar-senha");

                    if (Boolean.TRUE.equals(usuario.getPrecisaTrocarSenha()) && !isRotaTrocaSenha) {
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        response.setContentType("application/json; charset=UTF-8");
                        ErroPadraoDto erroDto = new ErroPadraoDto(
                                LocalDateTime.now(),
                                HttpStatus.FORBIDDEN.value(),
                                "Acesso Bloqueado",
                                List.of("Acesso negado. É obrigatório redefinir a senha provisória antes de continuar.")
                        );
                        response.getWriter().write(objectMapper.writeValueAsString(erroDto));
                        return;
                    }
                }

                var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}
