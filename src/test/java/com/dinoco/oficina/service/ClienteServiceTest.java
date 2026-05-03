package com.dinoco.oficina.service;

import com.dinoco.oficina.dto.ClienteRequestDto;
import com.dinoco.oficina.dto.ClienteResponseDto;
import com.dinoco.oficina.dto.EnderecoDto;
import com.dinoco.oficina.entity.Cliente;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.util.builders.ClienteBuilder;
import com.dinoco.oficina.util.builders.ClienteRequestDtoBuilder;
import com.dinoco.oficina.repository.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    @Captor
    private ArgumentCaptor<Cliente> clienteCaptor;

    @Test
    @DisplayName("Deve criar cliente Pessoa Física com sucesso usando Builder")
    void deveCriarClientePFComSucesso() {
        var request = ClienteRequestDtoBuilder.criarPessoaFisica("52998224725");
        var clienteMockSalvo = ClienteBuilder.umClientePF().comId(1L).build();
        when(repository.existsByDocumento(anyString())).thenReturn(false);
        when(repository.save(any(Cliente.class))).thenReturn(clienteMockSalvo);
        // Act
        ClienteResponseDto response = service.criar(request);
        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("João da Silva");
        verify(repository).save(clienteCaptor.capture());
        Cliente clienteSalvo = clienteCaptor.getValue();
        assertThat(clienteSalvo.getDocumento()).isEqualTo("52998224725");
        assertThat(clienteSalvo.getTipoPessoa()).isEqualTo("F");
        assertThat(clienteSalvo.getNome()).isEqualTo("João da Silva");
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cliente com documento já cadastrado")
    void deveLancarExcecaoQuandoDocumentoJaEstiverCadastrado() {
        var request = ClienteRequestDtoBuilder.criarPessoaFisica("52998224725");
        when(repository.existsByDocumento(anyString())).thenReturn(true);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.criar(request));
        assertEquals("Cliente já cadastrado com este documento.", exception.getMessage());
        verify(repository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF for inválido na criação")
    void deveLancarExcecaoQuandoDocumentoCpfForInvalido() {
        var request = ClienteRequestDtoBuilder.criarPessoaFisica("12345678900");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.criar(request));
        assertEquals("CPF inválido.", exception.getMessage());
        verify(repository, never()).existsByDocumento(anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CNPJ for inválido na criação")
    void deveLancarExcecaoQuandoDocumentoCnpjForInvalido() {
        var request = ClienteRequestDtoBuilder.criarPessoaJuridica("66666666666666");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.criar(request));
        assertEquals("CNPJ inválido.", exception.getMessage());
        verify(repository, never()).existsByDocumento(anyString());
    }

    @Test
    @DisplayName("Deve buscar ClienteResponseDto por ID com sucesso")
    void deveBuscarPorIdComSucesso() {
        Long id = 1L;
        Cliente clienteExistente = ClienteBuilder.umClientePF().comId(id).comNome("Maria").build();
        when(repository.findById(id)).thenReturn(Optional.of(clienteExistente));
        ClienteResponseDto response = service.buscarPorId(id);
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.nome()).isEqualTo("Maria");
    }

    @Test
    @DisplayName("Deve buscar entidade Cliente por ID com sucesso")
    void deveBuscarEntidadePorIdComSucesso() {
        Long id = 1L;
        Cliente clienteExistente = ClienteBuilder.umClientePJ().comId(id).build();
        when(repository.findById(id)).thenReturn(Optional.of(clienteExistente));
        Cliente resultado = service.buscarEntidadePorId(id);
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getTipoPessoa()).isEqualTo("J");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar Cliente por ID inexistente")
    void deveLancarExcecaoAoBuscarPorIdInexistente() {
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(id));
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarEntidadePorId(id));
    }

    @Test
    @DisplayName("Deve atualizar cliente e reescrever endereços com sucesso")
    void deveAtualizarClienteComSucesso() {
        // Arrange
        Long id = 1L;
        Cliente clienteExistente = ClienteBuilder.umClientePF()
                .comId(id)
                .comNome("Nome Antigo")
                .comDocumento("52998224725")
                .build();
        var enderecoDtoNovo = new EnderecoDto("89200111", "Rua Nova", "456", null, "Bairro", "Cidade", "SC");

        var requestAtualizacao = new ClienteRequestDto(
                "F", "52998224725", null, "Nome Novo", null, "novo.email@teste.com",
                "4788888888", List.of(enderecoDtoNovo)
        );

        when(repository.findById(id)).thenReturn(Optional.of(clienteExistente));
        when(repository.save(any(Cliente.class))).thenReturn(clienteExistente);
        // Act
        service.atualizar(id, requestAtualizacao);
        // Assert
        verify(repository).save(clienteCaptor.capture());
        Cliente clienteAtualizado = clienteCaptor.getValue();
        assertThat(clienteAtualizado.getNome()).isEqualTo("Nome Novo");
        assertThat(clienteAtualizado.getEmail()).isEqualTo("novo.email@teste.com");
        assertThat(clienteAtualizado.getEnderecos()).hasSize(1);
        assertThat(clienteAtualizado.getEnderecos().get(0).getCep()).isEqualTo("89200111");
        assertThat(clienteAtualizado.getEnderecos().get(0).getCliente()).isEqualTo(clienteAtualizado);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alterar documento do cliente na atualização")
    void deveLancarExcecaoAoAtualizarDocumento() {
        Long id = 1L;
        Cliente clienteExistente = ClienteBuilder.umClientePF().comDocumento("11111111111").build();
        var requestComDocDiferente = ClienteRequestDtoBuilder.criarPessoaFisica("22222222222");
        when(repository.findById(id)).thenReturn(Optional.of(clienteExistente));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.atualizar(id, requestComDocDiferente));
        assertEquals("Não é permitido alterar o documento (CPF/CNPJ) de um cliente já cadastrado.", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alterar tipo de pessoa do cliente na atualização")
    void deveLancarExcecaoAoAtualizarTipoPessoa() {
        Long id = 1L;
        // Cliente base é PF
        Cliente clienteExistente = ClienteBuilder.umClientePF().comTipoPessoa("F").build();

        // Requisição tenta mudar para PJ (J)
        ClienteRequestDto requestComTipoPessoaDiferente = new ClienteRequestDto(
                "J", clienteExistente.getDocumento(), null, "Nome", null, "email", "tel", Collections.emptyList());

        when(repository.findById(id)).thenReturn(Optional.of(clienteExistente));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.atualizar(id, requestComTipoPessoaDiferente));

        assertEquals("Não é permitido alterar o tipo de pessoa após o cadastro.", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve inativar cliente com sucesso")
    void deveDesativarClienteComSucesso() {
        Long id = 1L;
        Cliente cliente = ClienteBuilder.umClientePF().comId(id).build();
        when(repository.findById(id)).thenReturn(Optional.of(cliente));
        service.desativar(id);
        verify(repository).save(clienteCaptor.capture());
        Cliente clienteInativado = clienteCaptor.getValue();
        assertThat(clienteInativado.getAtivo()).isFalse();
    }
}