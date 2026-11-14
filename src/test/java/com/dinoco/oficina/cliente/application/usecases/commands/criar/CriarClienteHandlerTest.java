package com.dinoco.oficina.cliente.application.usecases.commands.criar;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;

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

    @Test
    void deveRejeitarDocumentoDuplicadoSemPersistir() {
        var command = comando("F", "52998224725", null);
        when(queryGateway.existePorDocumento(command.documento())).thenReturn(true);

        assertThatThrownBy(() -> handler.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cliente já cadastrado com este documento.");

        verifyNoInteractions(commandGateway);
    }

    @Test
    void deveRejeitarCpfInvalidoSemPersistir() {
        var command = comando("F", "52998224726", null);

        assertThatThrownBy(() -> handler.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CPF inválido.");

        verifyNoInteractions(commandGateway);
    }

    @Test
    void deveRejeitarCnpjInvalidoSemPersistir() {
        var command = comando("J", "19131243000198", null);

        assertThatThrownBy(() -> handler.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CNPJ inválido.");

        verifyNoInteractions(commandGateway);
    }

    @Test
    void deveCriarPessoaJuridicaComEnderecosEPreservarDadosNoOutput() {
        var endereco = new EnderecoCommand("89201000", "Rua A", "10", "Sala 2", "Centro", "Joinville", "SC");
        var segundo = new EnderecoCommand("89202000", "Rua B", "20", null, "América", "Joinville", "SC");
        var command = comando("J", "19131243000197", List.of(endereco, segundo));
        when(commandGateway.salvar(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente cliente = invocation.getArgument(0);
            cliente.setId(42L);
            return cliente;
        });

        var output = handler.executar(command);

        verify(queryGateway).existePorDocumento(command.documento());
        verify(commandGateway).salvar(clienteCaptor.capture());
        var salvo = clienteCaptor.getValue();
        assertThat(salvo.getId()).isEqualTo(42L);
        assertThat(salvo.getTipoPessoa()).isEqualTo("J");
        assertThat(salvo.getDocumento()).isEqualTo(command.documento());
        assertThat(salvo.getNome()).isEqualTo(command.nome());
        assertThat(salvo.getInscricaoEstadual()).isEqualTo(command.inscricaoEstadual());
        assertThat(salvo.getNomeFantasia()).isEqualTo(command.nomeFantasia());
        assertThat(salvo.getEmail()).isEqualTo(command.email());
        assertThat(salvo.getTelefone()).isEqualTo(command.telefone());
        assertThat(salvo.getAtivo()).isTrue();
        assertThat(salvo.getEnderecos()).hasSize(2);
        assertThat(salvo.getEnderecos().getFirst())
                .usingRecursiveComparison().isEqualTo(endereco);
        assertThat(salvo.getEnderecos().get(1))
                .usingRecursiveComparison().isEqualTo(segundo);
        assertThat(output).isEqualTo(new CriarClienteOutput(
                42L, command.tipoPessoa(), command.documento(), command.inscricaoEstadual(),
                command.nome(), command.nomeFantasia(), command.email(), command.telefone(), true,
                List.of(new EnderecoOutput("89201000", "Rua A", "10", "Sala 2", "Centro", "Joinville", "SC"),
                        new EnderecoOutput("89202000", "Rua B", "20", null, "América", "Joinville", "SC"))));
    }

    @Test
    void deveCriarSemEnderecosQuandoListaNula() {
        when(commandGateway.salvar(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var output = handler.executar(comando("F", "52998224725", null));

        verify(commandGateway).salvar(clienteCaptor.capture());
        assertThat(clienteCaptor.getValue().getEnderecos()).isEmpty();
        assertThat(output.enderecos()).isEmpty();
    }

    @Test
    void devePropagarFalhaAoSalvarSemRetornarSucesso() {
        var falha = new IllegalStateException("Persistência indisponível");
        when(commandGateway.salvar(any(Cliente.class))).thenThrow(falha);

        assertThatThrownBy(() -> handler.executar(comando("F", "52998224725", null)))
                .isSameAs(falha);
    }

    private CriarClienteCommand comando(String tipoPessoa, String documento, List<EnderecoCommand> enderecos) {
        return new CriarClienteCommand(tipoPessoa, documento, "123456", "Cliente Teste",
                "Nome Fantasia", "cliente@email.com", "47999999999", enderecos);
    }
}