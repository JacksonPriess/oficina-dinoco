package com.dinoco.oficina.autenticacao.infrastructure.security;

import com.dinoco.oficina.autenticacao.infrastructure.gateways.*;
import com.dinoco.oficina.autenticacao.infrastructure.config.AutenticacaoConfig;
import com.dinoco.oficina.autenticacao.infrastructure.persistence.*;
import com.dinoco.oficina.autenticacao.infrastructure.web.*;
import com.dinoco.oficina.autenticacao.infrastructure.web.dto.*;
import com.dinoco.oficina.autenticacao.infrastructure.web.mapper.AutenticacaoWebMapper;
import com.dinoco.oficina.funcionario.infrastructure.gateways.UsuarioSistemaGatewayImpl;
import com.dinoco.oficina.funcionario.infrastructure.persistence.UsuarioJpaRepository;
import com.dinoco.oficina.funcionario.domain.PerfilUsuario;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.mock.web.*;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.json.JsonMapper;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CredenciaisTest {
    @Test void loginGeraTokenETrocaSenhaPersisteSomenteHash() {
        var manager = mock(AuthenticationManager.class);
        when(manager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken("admin", null, List.of()));
        var repository = mock(UsuarioRepository.class);
        var user = new UsuarioEntity(); user.setId(7L); user.setLogin("admin");
        when(repository.findById(7L)).thenReturn(Optional.of(user));
        when(repository.findByLogin("admin")).thenReturn(user);
        var tokens = new TokenService(); ReflectionTestUtils.setField(tokens, "secret", "segredo-teste");
        var encoder = new BCryptPasswordEncoder(4);
        var config = new AutenticacaoConfig();
        var credentials = new UsuarioAutenticacaoGatewayImpl(repository);
        var controller = new AutenticacaoSpringWebController(config.autenticacaoControllerClean(
                config.realizarLoginUseCase(new AutenticacaoCommandGatewayImpl(manager), new TokenGatewayImpl(tokens)),
                config.trocarSenhaUseCase(credentials, new PasswordEncoderGatewayImpl(encoder))), Mappers.getMapper(AutenticacaoWebMapper.class));
        var token = controller.login(new LoginDto("admin", "senha")).getBody().token();
        assertThat(tokens.validarToken(token).orElseThrow().subject()).isEqualTo("admin");
        assertThat(controller.trocarSenha(new TrocarSenhaRequestDto("SenhaNova123!"), user).getStatusCode().value()).isEqualTo(204);
        assertThat(encoder.matches("SenhaNova123!", user.getSenha())).isTrue();
        assertThat(user.getPrecisaTrocarSenha()).isFalse();
        verify(repository).save(user);
        assertThat(new AutenticacaoService(repository).loadUserByUsername("admin")).isSameAs(user);
        assertThatThrownBy(() -> credentials.atualizarCredenciais(99L, "hash", false)).isInstanceOf(RecursoNaoEncontradoException.class);
    }
    @Test void criarEResetarAcessoExigeNovaSenhaECriptografa() {
        var repository = mock(UsuarioJpaRepository.class);
        var encoder = new BCryptPasswordEncoder(4);
        var gateway = new UsuarioSistemaGatewayImpl(repository, encoder);
        var captured = org.mockito.ArgumentCaptor.forClass(UsuarioEntity.class);
        when(repository.save(any())).thenAnswer(i -> { var u = i.<UsuarioEntity>getArgument(0); u.setId(7L); return u; });
        assertThat(gateway.criarAcesso("ana", "Inicial123!", PerfilUsuario.ATENDENTE)).isEqualTo(7L);
        verify(repository).save(captured.capture());
        var user = captured.getValue();
        assertThat(encoder.matches("Inicial123!", user.getSenha())).isTrue();
        assertThat(user.getPrecisaTrocarSenha()).isTrue();
        when(repository.findById(7L)).thenReturn(Optional.of(user));
        String temporary = gateway.resetarSenha(7L);
        assertThat(temporary).matches("Oficina@[0-9]{4}");
        assertThat(encoder.matches(temporary, user.getSenha())).isTrue();
        when(repository.existsByLogin("ana")).thenReturn(true);
        assertThatThrownBy(() -> gateway.criarAcesso("ana", "senha", PerfilUsuario.ADMIN)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> gateway.resetarSenha(99L)).isInstanceOf(RecursoNaoEncontradoException.class);
    }
    @Test void distinguirNaoAutenticadoDeSemPermissao() throws Exception {
        var mapper = JsonMapper.builder().build();
        var response = new MockHttpServletResponse();
        new CustomAuthenticationEntryPoint(mapper).commence(new MockHttpServletRequest(), response, new BadCredentialsException("detalhe interno"));
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("Não autenticado").doesNotContain("detalhe interno");
        response = new MockHttpServletResponse();
        new CustomAccessDeniedHandler(mapper).handle(new MockHttpServletRequest(), response, new AccessDeniedException("detalhe interno"));
        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentAsString()).contains("Acesso negado").doesNotContain("detalhe interno");
    }
}
