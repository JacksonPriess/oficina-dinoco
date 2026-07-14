package com.dinoco.oficina.cliente.application.usecases.commands.desativar;

import com.dinoco.oficina.cliente.application.gateways.ClienteCommandGateway;
import com.dinoco.oficina.cliente.domain.Cliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DesativarClienteHandlerTest {

    @Mock
    private ClienteCommandGateway clienteCommandGateway;

    @InjectMocks
    private DesativarClienteHandler handler;

    @Captor
    private ArgumentCaptor<Cliente> clienteCaptor;

    @Test
    @DisplayName("Deve desativar cliente com sucesso")
    void deveDesativarClienteComSucesso() {
        // Arrange
        Long idCliente = 1L;

        var command = new DesativarClienteCommand(
                idCliente
        );

        var clienteExistente = new Cliente(
                idCliente, "F", "52998224725", "João da Silva", null,
                null, "joao@email.com", "11999999999", true, null, new ArrayList<>()
        );

        // Cliente como esperamos que ele saia do gateway após salvar
        var clienteMockSalvo = new Cliente(
                idCliente, "F", "52998224725", "João da Silva", null,
                null, "joao.novo@email.com", "11999999999", false, null, new ArrayList<>()
        );

        when(clienteCommandGateway.buscarParaAlteracao(idCliente)).thenReturn(Optional.of(clienteExistente));
        when(clienteCommandGateway.salvar(any(Cliente.class))).thenReturn(clienteMockSalvo);

        // Act
        handler.executar(command);

        // Assert
        verify(clienteCommandGateway, times(1)).salvar(clienteCaptor.capture());

        Cliente clienteSalvo = clienteCaptor.getValue();
        assertThat(clienteSalvo.getAtivo()).isFalse();
    }

    @Test
    @DisplayName("Deve lançar exceção quando o cliente não for encontrado para desativar")
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        // Arrange
        Long idCliente = 99L;
        var command = new DesativarClienteCommand(idCliente);

        when(clienteCommandGateway.buscarParaAlteracao(idCliente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> handler.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cliente não encontrado.");

        // Garante que o método salvar nunca foi chamado
        verify(clienteCommandGateway, never()).salvar(any());
    }
}