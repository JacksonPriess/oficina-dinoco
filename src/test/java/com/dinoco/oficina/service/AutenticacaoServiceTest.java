package com.dinoco.oficina.service;

import com.dinoco.oficina.entity.Usuario;
import com.dinoco.oficina.repository.UsuarioRepository;
import com.dinoco.oficina.util.builders.UsuarioBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private AutenticacaoService service;

    @Test
    @DisplayName("Deve carregar usuário pelo username (login) com sucesso")
    void deveCarregarUsuarioComSucesso() {
        // Arrange
        String loginProcurado = "funcionario.teste";
        Usuario usuarioMock = UsuarioBuilder.umUsuario().build();
        when(repository.findByLogin(loginProcurado)).thenReturn(usuarioMock);
        // Act
        UserDetails resultado = service.loadUserByUsername(loginProcurado);
        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsername()).isEqualTo(loginProcurado);
        assertThat(resultado.getPassword()).isEqualTo("senhaCriptografada123");
        verify(repository, times(1)).findByLogin(loginProcurado);
    }

    @Test
    @DisplayName("Deve lidar com usuário não encontrado")
    void deveRetornarNuloQuandoUsuarioNaoExiste() {
        // Arrange
        String loginInexistente = "login.fantasma";
        when(repository.findByLogin(loginInexistente)).thenReturn(null);
        // Act
        UserDetails resultado = service.loadUserByUsername(loginInexistente);
        // Assert
        assertThat(resultado).isNull();
        verify(repository).findByLogin(loginInexistente);
    }
}