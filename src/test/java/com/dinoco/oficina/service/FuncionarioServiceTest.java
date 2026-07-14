package com.dinoco.oficina.service;
/*
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import java.util.Optional;
import com.dinoco.oficina.dto.FuncionarioRequestDto;
import com.dinoco.oficina.dto.FuncionarioResponseDto;
import com.dinoco.oficina.entity.Funcionario;
import com.dinoco.oficina.enums.CargoFuncionario;
import com.dinoco.oficina.enums.PerfilUsuario;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.repository.FuncionarioRepository;
import com.dinoco.oficina.util.builders.FuncionarioBuilder;
import com.dinoco.oficina.util.builders.FuncionarioRequestDtoBuilder;
import com.dinoco.oficina.util.builders.UsuarioBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository repository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private FuncionarioService service;

    @Captor
    private ArgumentCaptor<Funcionario> funcionarioCaptor;

    @Test
    @DisplayName("Deve criar funcionário SEM acesso ao sistema com sucesso")
    void deveCriarFuncionarioSemAcessoComSucesso() {
        var request = FuncionarioRequestDtoBuilder.criarSemAcesso("52998224725");
        var funcionarioMock = FuncionarioBuilder.umFuncionario().build();

        when(repository.existsByCpf(anyString())).thenReturn(false);
        when(repository.save(any(Funcionario.class))).thenReturn(funcionarioMock);

        FuncionarioResponseDto response = service.criar(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);

        verify(repository).save(funcionarioCaptor.capture());
        Funcionario capturado = funcionarioCaptor.getValue();

        assertThat(capturado.getUsuarioId()).isNull();
        verify(usuarioService, never()).criarUsuarioSistema(any(), any(), any());
    }

    @Test
    @DisplayName("Deve criar funcionário COM acesso ao sistema com sucesso")
    void deveCriarFuncionarioComAcessoComSucesso() {
        var request = FuncionarioRequestDtoBuilder.criarComAcesso("52998224725", "mecanico.silva", "senha123");
        var usuarioSalvoMock = UsuarioBuilder.umUsuario().comId(99L).build();
        var funcionarioMock = FuncionarioBuilder.umFuncionario().comUsuarioId(99L).build();

        when(repository.existsByCpf(anyString())).thenReturn(false);
        when(usuarioService.criarUsuarioSistema(request.login(), request.senha(), PerfilUsuario.MECANICO)).thenReturn(usuarioSalvoMock);
        when(repository.save(any(Funcionario.class))).thenReturn(funcionarioMock);

        FuncionarioResponseDto response = service.criar(request);

        assertThat(response).isNotNull();

        verify(usuarioService).criarUsuarioSistema(request.login(), request.senha(), PerfilUsuario.MECANICO);
        verify(repository).save(funcionarioCaptor.capture());

        Funcionario capturado = funcionarioCaptor.getValue();
        assertThat(capturado.getUsuarioId()).isEqualTo(99L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar funcionário com CPF já cadastrado")
    void deveLancarExcecaoQuandoCpfJaExistir() {
        var request = FuncionarioRequestDtoBuilder.criarSemAcesso("52998224725");

        when(repository.existsByCpf(anyString())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.criar(request));

        assertEquals("CPF já cadastrado.", exception.getMessage());
        verify(repository, never()).save(any());
        verify(usuarioService, never()).criarUsuarioSistema(any(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar funcionário com CPF inválido")
    void deveLancarExcecaoQuandoCpfInvalido() {
        var request = FuncionarioRequestDtoBuilder.criarSemAcesso("12345678900");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.criar(request));

        assertEquals("CPF inválido.", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar FuncionarioResponseDto por ID com sucesso")
    void deveBuscarPorIdComSucesso() {
        Long id = 1L;
        Funcionario funcionario = FuncionarioBuilder.umFuncionario().comId(id).build();

        when(repository.findById(id)).thenReturn(Optional.of(funcionario));

        FuncionarioResponseDto response = service.buscarPorId(id);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.cpf()).isEqualTo(funcionario.getCpf());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar Funcionário por ID inexistente")
    void deveLancarExcecaoAoBuscarPorIdInexistente() {
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(id));
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarEntidadePorId(id));
    }

    @Test
    @DisplayName("Deve atualizar funcionário com sucesso")
    void deveAtualizarFuncionarioComSucesso() {
        Long id = 1L;
        Funcionario funcionarioExistente = FuncionarioBuilder.umFuncionario()
                .comId(id)
                .comCpf("52998224725")
                .build();

        var requestAtualizacao = new FuncionarioRequestDto(
                "Nome Modificado", "52998224725", CargoFuncionario.ATENDENTE, false, null, null
        );

        when(repository.findById(id)).thenReturn(Optional.of(funcionarioExistente));
        when(repository.save(any(Funcionario.class))).thenReturn(funcionarioExistente);

        service.atualizar(id, requestAtualizacao);

        verify(repository).save(funcionarioCaptor.capture());
        Funcionario atualizado = funcionarioCaptor.getValue();

        assertThat(atualizado.getNome()).isEqualTo("Nome Modificado");
        assertThat(atualizado.getCargo()).isEqualTo(CargoFuncionario.ATENDENTE);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alterar CPF do funcionário")
    void deveLancarExcecaoAoAtualizarCpf() {
        Long id = 1L;
        Funcionario funcionarioExistente = FuncionarioBuilder.umFuncionario()
                .comCpf("11111111111")
                .build();

        var requestComCpfDiferente = FuncionarioRequestDtoBuilder.criarSemAcesso("22222222222");

        when(repository.findById(id)).thenReturn(Optional.of(funcionarioExistente));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.atualizar(id, requestComCpfDiferente));

        assertEquals("Não é permitido alterar o cpf de um funcionário já cadastrado.", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve inativar funcionário com sucesso")
    void deveDesativarFuncionarioComSucesso() {
        Long id = 1L;
        Funcionario funcionario = FuncionarioBuilder.umFuncionario().comId(id).build();

        when(repository.findById(id)).thenReturn(Optional.of(funcionario));

        service.desativar(id);

        verify(repository).save(funcionarioCaptor.capture());
        Funcionario inativado = funcionarioCaptor.getValue();

        assertThat(inativado.isAtivo()).isFalse();
    }

    @Test
    @DisplayName("Deve resetar senha do funcionário com sucesso")
    void deveResetarSenhaComSucesso() {
        Long id = 1L;
        Long usuarioId = 99L;
        Funcionario funcionario = FuncionarioBuilder.umFuncionario()
                .comId(id)
                .comUsuarioId(usuarioId)
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(funcionario));
        when(usuarioService.resetarSenhaGerandoTemporaria(usuarioId)).thenReturn("Oficina@1234");

        String senhaGerada = service.resetarSenhaFuncionario(id);

        assertThat(senhaGerada).isEqualTo("Oficina@1234");
        verify(usuarioService).resetarSenhaGerandoTemporaria(usuarioId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao resetar senha de funcionário sem acesso ao sistema")
    void deveLancarExcecaoAoResetarSenhaDeFuncionarioSemAcesso() {
        Long id = 1L;
        // Funcionário sem usuarioId configurado
        Funcionario funcionario = FuncionarioBuilder.umFuncionario()
                .comId(id)
                .comUsuarioId(null)
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(funcionario));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.resetarSenhaFuncionario(id));

        assertEquals("Este funcionário não possui acesso ao sistema.", exception.getMessage());
        verify(usuarioService, never()).resetarSenhaGerandoTemporaria(anyLong());
    }
}

 */