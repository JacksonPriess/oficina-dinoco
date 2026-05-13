package com.dinoco.oficina.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Optional;
import com.dinoco.oficina.entity.Usuario;
import com.dinoco.oficina.enums.PerfilUsuario;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;

    @Test
    @DisplayName("Deve criar usuário com sucesso quando o login for inédito")
    void deveCriarUsuarioComSucesso() {
        // 1. Arrange
        String login = "john.doerr";
        String senhaPura = "senha123";
        String senhaCripto = "encoded_senha";
        PerfilUsuario perfil = PerfilUsuario.MECANICO;

        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setLogin(login);
        when(usuarioRepository.existsByLogin(login)).thenReturn(false);
        when(passwordEncoder.encode(senhaPura)).thenReturn(senhaCripto);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);
        // 2. Act
        Usuario resultado = usuarioService.criarUsuarioSistema(login, senhaPura, perfil);
        // 3. Assert
        assertNotNull(resultado);
        assertEquals(login, resultado.getLogin());
        verify(usuarioRepository, times(1)).existsByLogin(login);
        verify(passwordEncoder, times(1)).encode(senhaPura);
        verify(usuarioRepository, times(1)).save(usuarioCaptor.capture());
        Usuario entidadeSalva = usuarioCaptor.getValue();
        assertEquals(login, entidadeSalva.getLogin());
        assertEquals(perfil, entidadeSalva.getPerfil());
        assertEquals(senhaCripto, entidadeSalva.getSenha());

    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar usuário com login já existente")
    void deveLancarExcecaoQuandoLoginJaExiste() {
        // Arrange
        String login = "login.existente";
        when(usuarioRepository.existsByLogin(login)).thenReturn(true);
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.criarUsuarioSistema(login, "qualquerSenha", PerfilUsuario.MECANICO);
        });
        assertEquals("Este login já está em uso.", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve resetar senha gerando uma senha temporária válida")
    void deveResetarSenhaComSucesso() {
        // 1. Arrange
        Long usuarioId = 123L;
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(usuarioId);
        usuarioMock.setLogin("john");
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.encode(anyString())).thenReturn("senha_temporaria_cripto");
        // 2. Act
        String senhaGerada = usuarioService.resetarSenhaGerandoTemporaria(usuarioId);
        // 3. Assert
        assertNotNull(senhaGerada);
        assertTrue(senhaGerada.startsWith("Oficina@"));
        assertEquals(12, senhaGerada.length());
        verify(usuarioRepository).save(usuarioCaptor.capture());
        Usuario usuarioAtualizado = usuarioCaptor.getValue();
        assertEquals("senha_temporaria_cripto", usuarioAtualizado.getSenha());
        verify(passwordEncoder).encode(startsWith("Oficina@"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao resetar senha de usuário inexistente")
    void deveLancarExcecaoAoResetarSenhaUsuarioInexistente() {
        // Arrange
        Long idInexistente = 999L;
        when(usuarioRepository.findById(idInexistente)).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(RecursoNaoEncontradoException.class, () -> {
            usuarioService.resetarSenhaGerandoTemporaria(idInexistente);
        });
        verify(usuarioRepository, never()).save(any());
    }
}