package com.dinoco.oficina.funcionario.application.usecases.commands.criar;

import com.dinoco.oficina.funcionario.application.gateways.FuncionarioCommandGateway;
import com.dinoco.oficina.funcionario.application.gateways.FuncionarioQueryGateway;
import com.dinoco.oficina.funcionario.domain.CargoFuncionario;
import com.dinoco.oficina.funcionario.domain.Funcionario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//20:52

@ExtendWith(MockitoExtension.class)
class CriarFuncionarioHandlerTest {

    @Mock
    private FuncionarioCommandGateway commandGateway;

    @Mock
    private FuncionarioQueryGateway queryGateway;

    @InjectMocks
    private CriarFuncionarioHandler handler;

    @Captor
    private ArgumentCaptor<Funcionario> funcionarioCaptor;

    @Test
    void deveCriarFuncionarioComSucesso() {
        //Arrange
        var command = new CriarFuncionarioCommand("João Silva", "00000000191", CargoFuncionario.MECANICO,
                true, "joao.silva", "@20");

        //Instancia o dominio esperado que sera retornado pelo mock de salvamento
        var funcionarioMockSalvo = new Funcionario(1L, "João Silva", "00000000191", CargoFuncionario.MECANICO, true, 2l);

        // Simulamos o comportamento das portas de saída
        when(queryGateway.existePorCpf(anyString())).thenReturn(false);
        when(commandGateway.salvar(any(Funcionario.class))).thenReturn(funcionarioMockSalvo);

        // Act
        CriarFuncionarioOutput response = handler.executar(command);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("João Silva");

        // Verificamos se o gateway de escrita foi chamado capturando o objeto de domínio puro
        verify(commandGateway).salvar(funcionarioCaptor.capture());
        Funcionario funcionario = funcionarioCaptor.getValue();
        // Assertions garantem que o Handler montou a Entidade de Domínio corretamente
        assertThat(funcionario.getCpf()).isEqualTo("00000000191");
        assertThat(funcionario.getCargo()).isEqualTo(CargoFuncionario.MECANICO);
        assertThat(funcionario.getNome()).isEqualTo("João da Silva");
        assertThat(funcionario.isAtivo()).isTrue();

    }
}