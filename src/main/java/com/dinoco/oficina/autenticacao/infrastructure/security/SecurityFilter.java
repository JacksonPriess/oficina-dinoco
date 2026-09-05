package com.dinoco.oficina.autenticacao.infrastructure.security;

import com.dinoco.oficina.autenticacao.infrastructure.persistence.UsuarioEntity;
import com.dinoco.oficina.exception.ErroPadraoDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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
            var tokenValidado = tokenService.validarToken(token);
            if (tokenValidado.isPresent()) {
                var tokenAutenticado = tokenValidado.get();
                if (tokenAutenticado.isCliente()) {
                    autenticarCliente(tokenAutenticado);
                } else {
                    boolean autenticado = autenticarFuncionario(tokenAutenticado, request, response);
                    if (!autenticado) {
                        return;
                    }
                }
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

    private boolean autenticarFuncionario(TokenAutenticado token, HttpServletRequest request, HttpServletResponse response) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(token.subject());
        if (userDetails instanceof UsuarioEntity usuario) {
            String uri = request.getRequestURI();
            boolean isRotaTrocaSenha = uri.equals("/api/auth/trocar-senha");

            if (Boolean.TRUE.equals(usuario.getPrecisaTrocarSenha()) && !isRotaTrocaSenha) {
                log.warn("Acesso bloqueado por senha provisória. usuario={}, uri={}", token.subject(), uri);
                try {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    ErroPadraoDto erroDto =
                            new ErroPadraoDto(
                                    LocalDateTime.now(),
                                    HttpStatus.FORBIDDEN.value(),
                                    "Acesso Bloqueado",
                                    List.of("Acesso negado. É obrigatório redefinir a senha provisória antes de continuar."));

                    response.getWriter().write(objectMapper.writeValueAsString(erroDto));
                    response.getWriter().flush();

                    return false;
                } catch (IOException e) {
                    log.error("Erro ao escrever resposta de acesso bloqueado", e);
                    throw new RuntimeException(e);
                }
            }
        }
        var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return true;
    }

    private void autenticarCliente(TokenAutenticado token) {
        Long clienteId;
        try {
            clienteId = Long.valueOf(token.subject());
        } catch (NumberFormatException exception) {
            return;
        }
        ClientePrincipal principal = new ClientePrincipal(clienteId);
        var authentication = new UsernamePasswordAuthenticationToken(principal, null, List.of(new SimpleGrantedAuthority("ROLE_CLIENTE")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
