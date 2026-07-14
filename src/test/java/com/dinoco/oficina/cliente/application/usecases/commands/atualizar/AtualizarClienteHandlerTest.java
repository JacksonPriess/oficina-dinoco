package com.dinoco.oficina.cliente.application.usecases.commands.atualizar;

import com.dinoco.oficina.cliente.application.gateways.ClienteCommandGateway;
import com.dinoco.oficina.cliente.domain.Cliente;
import com.dinoco.oficina.cliente.domain.Endereco;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarClienteHandlerTest {

    @Mock
    private ClienteCommandGateway clienteCommandGateway;

    @InjectMocks
    private AtualizarClienteHandler handler;

    @Captor
    private ArgumentCaptor<Cliente> clienteCaptor;

    @Test
    @DisplayName("Deve atualizar cliente com sucesso")
    void deveAtualizarClienteComSucesso() {
        // Arrange
        Long idCliente = 1L;
        var enderecoCommand = new EnderecoCommand("01000-000", "Rua Direita", "123", "Sala 1", "Centro", "São Paulo", "SP");

        var command = new AtualizarClienteCommand(
                idCliente, "F", "52998224725", null, "João da Silva Atualizado",
                "João Novo", "joao.novo@email.com", "11988888888", List.of(enderecoCommand)
        );

        // Cliente como ele está no banco antes da atualização
        var clienteExistente = new Cliente(
                idCliente, "F", "52998224725", "João da Silva", null,
                "João", "joao@email.com", "11999999999", true, null, new ArrayList<>()
        );

        // Cliente como esperamos que ele saia do gateway após salvar
        var enderecoSalvo = new Endereco("01000-000", "Rua Direita", "123", "Sala 1", "Centro", "São Paulo", "SP");
        var clienteMockSalvo = new Cliente(
                idCliente, "F", "52998224725", "João da Silva Atualizado", null,
                "João Novo", "joao.novo@email.com", "11988888888", true, null, List.of(enderecoSalvo)
        );

        when(clienteCommandGateway.buscarParaAlteracao(idCliente)).thenReturn(Optional.of(clienteExistente));
        when(clienteCommandGateway.salvar(any(Cliente.class))).thenReturn(clienteMockSalvo);

        // Act
        AtualizarClienteOutput response = handler.executar(command);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(idCliente);
        assertThat(response.nome()).isEqualTo("João da Silva Atualizado");
        assertThat(response.email()).isEqualTo("joao.novo@email.com");
        assertThat(response.enderecos()).hasSize(1);
        assertThat(response.enderecos().get(0).cep()).isEqualTo("01000-000");

        // Verificamos o objeto de domínio enviado para o gateway de salvamento
        verify(clienteCommandGateway).salvar(clienteCaptor.capture());
        Cliente clienteEnviadoParaSalvar = clienteCaptor.getValue();

        assertThat(clienteEnviadoParaSalvar.getNome()).isEqualTo("João da Silva Atualizado");
        assertThat(clienteEnviadoParaSalvar.getEmail()).isEqualTo("joao.novo@email.com");
        assertThat(clienteEnviadoParaSalvar.getEnderecos()).hasSize(1);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o cliente não for encontrado")
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        // Arrange
        var command = new AtualizarClienteCommand(
                99L, "F", "52998224725", null, "João", "João", "joao@email.com", "11999999999", Collections.emptyList()
        );

        when(clienteCommandGateway.buscarParaAlteracao(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> handler.executar(command))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Cliente não encontrado.");

        verify(clienteCommandGateway, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alterar o documento do cliente")
    void deveLancarExcecaoAoTentarAlterarDocumento() {
        // Arrange
        Long idCliente = 1L;
        var command = new AtualizarClienteCommand(
                idCliente, "F", "11122233344", null, "João", "João", "joao@email.com", "11999999999", Collections.emptyList()
        );

        var clienteExistente = new Cliente(
                idCliente, "F", "52998224725", "João da Silva", null,
                "João", "joao@email.com", "11999999999", true, null, Collections.emptyList()
        );

        when(clienteCommandGateway.buscarParaAlteracao(idCliente)).thenReturn(Optional.of(clienteExistente));

        // Act & Assert
        assertThatThrownBy(() -> handler.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Não é permitido alterar o documento (CPF/CNPJ) de um cliente já cadastrado.");

        verify(clienteCommandGateway, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alterar o tipo de pessoa do cliente")
    void deveLancarExcecaoAoTentarAlterarTipoPessoa() {
        // Arrange
        Long idCliente = 1L;
        var command = new AtualizarClienteCommand(
                idCliente, "J", "52998224725", null, "João", "João", "joao@email.com", "11999999999", Collections.emptyList()
        );

        var clienteExistente = new Cliente(
                idCliente, "F", "52998224725", "João da Silva", null,
                "João", "joao@email.com", "11999999999", true, null, Collections.emptyList()
        );

        when(clienteCommandGateway.buscarParaAlteracao(idCliente)).thenReturn(Optional.of(clienteExistente));

        // Act & Assert
        assertThatThrownBy(() -> handler.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Não é permitido alterar o tipo de pessoa após o cadastro.");

        verify(clienteCommandGateway, never()).salvar(any());
    }
}