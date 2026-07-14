package com.dinoco.oficina.cliente.application.usecases.commands.criar;

import com.dinoco.oficina.cliente.application.gateways.ClienteCommandGateway;
import com.dinoco.oficina.cliente.application.gateways.ClienteQueryGateway;
import com.dinoco.oficina.cliente.domain.Cliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarClienteHandlerTest {

    @Mock
    private ClienteCommandGateway commandGateway;

    @Mock
    private ClienteQueryGateway queryGateway;

    @InjectMocks
    private CriarClienteHandler handler;

    @Captor
    private ArgumentCaptor<Cliente> clienteCaptor;

    @Test
    @DisplayName("Deve criar cliente Pessoa Física com sucesso")
    void deveCriarClientePFComSucesso() {
        // Arrange
        var command = new CriarClienteCommand(
                "F", "52998224725", null, "João da Silva",
                "João", "joao@email.com", "47999999999", Collections.emptyList()
        );

        // Instanciamos o domínio esperado que será retornado pelo mock de salvamento
        var clienteMockSalvo = new Cliente(
                1L, "F", "52998224725", "João da Silva", null,
                "João", "joao@email.com", "47999999999", true, null, Collections.emptyList()
        );

        // Simulamos o comportamento das portas de saída
        when(queryGateway.existePorDocumento(anyString())).thenReturn(false);
        when(commandGateway.salvar(any(Cliente.class))).thenReturn(clienteMockSalvo);

        // Act
        CriarClienteOutput response = handler.executar(command);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("João da Silva");

        // Verificamos se o gateway de escrita foi chamado capturando o objeto de domínio puro
        verify(commandGateway).salvar(clienteCaptor.capture());
        Cliente clienteSalvo = clienteCaptor.getValue();
        // Assertions garantem que o Handler montou a Entidade de Domínio corretamente
        assertThat(clienteSalvo.getDocumento()).isEqualTo("52998224725");
        assertThat(clienteSalvo.getTipoPessoa()).isEqualTo("F");
        assertThat(clienteSalvo.getNome()).isEqualTo("João da Silva");
        assertThat(clienteSalvo.getDataCriacao()).isNotNull();
        assertThat(clienteSalvo.getAtivo()).isTrue();
    }
}