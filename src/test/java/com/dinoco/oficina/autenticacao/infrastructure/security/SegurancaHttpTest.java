package com.dinoco.oficina.autenticacao.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.dinoco.oficina.autenticacao.infrastructure.persistence.UsuarioEntity;
import com.dinoco.oficina.funcionario.domain.PerfilUsuario;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.json.JsonMapper;
import java.time.Instant;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class SegurancaHttpTest {
    private static final String SECRET = "somente-teste-nao-usar-em-producao";
    private final TokenService tokens = new TokenService();
    private final UserDetailsService usuarios = mock(UserDetailsService.class);
    private final FilterChain chain = mock(FilterChain.class);
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/clientes");
    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private SecurityFilter filter;

    @BeforeEach void configurar() {
        ReflectionTestUtils.setField(tokens, "secret", SECRET);
        filter = new SecurityFilter(tokens, usuarios, JsonMapper.builder().build());
        SecurityContextHolder.clearContext();
    }
    @AfterEach void limpar() { SecurityContextHolder.clearContext(); }

    @Test void tokenGeradoPodeSerValidado() {
        var token = tokens.validarToken(tokens.gerarToken("admin")).orElseThrow();
        assertThat(token.subject()).isEqualTo("admin");
        assertThat(token.isCliente()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"expirado", "outra-assinatura", "outro-emissor"})
    void rejeitarTokensInvalidos(String motivo) {
        var jwt = JWT.create().withIssuer(motivo.equals("outro-emissor") ? "outra-api" : "oficina-api")
                .withSubject("admin").withExpiresAt(Instant.now().plusSeconds(motivo.equals("expirado") ? -60 : 60))
                .sign(Algorithm.HMAC256(motivo.equals("outra-assinatura") ? "outro-segredo" : SECRET));
        assertThat(tokens.validarToken(jwt)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "Basic abc", "Bearer invalido"})
    void credencialAusenteOuInvalidaNaoAutentica(String header) throws Exception {
        if (!header.isEmpty()) request.addHeader("Authorization", header);
        filter.doFilter(request, response, chain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(request, response);
        verifyNoInteractions(usuarios);
    }

    @Test void autenticarClienteSemConsultarUsuarioFuncionario() throws Exception {
        request.addHeader("Authorization", "Bearer " + JWT.create().withIssuer("oficina-api").withSubject("42")
                .withClaim("tipo", "CLIENTE").sign(Algorithm.HMAC256(SECRET)));
        filter.doFilter(request, response, chain);
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth.getPrincipal()).isEqualTo(new ClientePrincipal(42L));
        assertThat(auth.getAuthorities()).extracting("authority").containsExactly("ROLE_CLIENTE");
        verifyNoInteractions(usuarios);
    }

    @Test void subjectClienteInvalidoNaoAutentica() throws Exception {
        request.addHeader("Authorization", "Bearer " + JWT.create().withIssuer("oficina-api").withSubject("texto")
                .withClaim("tipo", "CLIENTE").sign(Algorithm.HMAC256(SECRET)));
        filter.doFilter(request, response, chain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(request, response);
    }

    @Test void senhaProvisoriaBloqueiaAcessoExcetoTrocaSenha() throws Exception {
        var user = usuario(true);
        when(usuarios.loadUserByUsername("admin")).thenReturn(user);
        request.addHeader("Authorization", "Bearer " + tokens.gerarToken("admin"));
        filter.doFilter(request, response, chain);
        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentAsString()).contains("redefinir");
        verifyNoInteractions(chain);
        request.setRequestURI("/api/auth/trocar-senha");
        filter.doFilter(request, new MockHttpServletResponse(), chain);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isSameAs(user);
    }

    @Test void autenticarFuncionarioComSenhaDefinitiva() throws Exception {
        var user = usuario(false);
        when(usuarios.loadUserByUsername("admin")).thenReturn(user);
        request.addHeader("Authorization", "Bearer " + tokens.gerarToken("admin"));
        filter.doFilter(request, response, chain);
        assertThat(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()).isTrue();
        verify(chain).doFilter(request, response);
    }

    private UsuarioEntity usuario(boolean provisoria) {
        var user = new UsuarioEntity();
        user.setLogin("admin"); user.setPerfil(PerfilUsuario.ADMIN); user.setPrecisaTrocarSenha(provisoria);
        return user;
    }
}
