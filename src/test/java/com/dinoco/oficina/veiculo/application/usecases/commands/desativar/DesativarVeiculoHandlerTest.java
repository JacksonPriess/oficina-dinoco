package com.dinoco.oficina.veiculo.application.usecases.commands.desativar;

import com.dinoco.oficina.veiculo.application.gateways.VeiculoCommandGateway;
import com.dinoco.oficina.veiculo.domain.Veiculo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DesativarVeiculoHandlerTest {

    @Mock
    private VeiculoCommandGateway veiculoCommandGateway;

    @InjectMocks
    private DesativarVeiculoHandler handler;

    @Captor
    private ArgumentCaptor<Veiculo> veiculoCaptor;

    @Test
    @DisplayName("Deve desativar veiculo com sucesso")
    void deveDesativarVeiculoComSucesso() {
        // Arrange
        Long idVeiculo = 1L;
        var command = new DesativarVeiculoCommand(idVeiculo);

        // Criamos um mock do veículo para simular o comportamento do domínio
        // ou uma instância real caso o construtor seja simples.
        // Assumindo que o método desativar() altera um estado interno.
        var veiculoExistente = mock(Veiculo.class);

        when(veiculoCommandGateway.buscarParaAlteracao(idVeiculo)).thenReturn(Optional.of(veiculoExistente));

        // Act
        handler.executar(command);

        // Assert
        // Verifica se o método desativar do domínio foi chamado
        verify(veiculoExistente, times(1)).desativar();

        // Verifica se o gateway salvou o objeto
        verify(veiculoCommandGateway, times(1)).salvar(veiculoCaptor.capture());

        Veiculo veiculoSalvo = veiculoCaptor.getValue();
        assertThat(veiculoSalvo).isEqualTo(veiculoExistente);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o veiculo não for encontrado para desativar")
    void deveLancarExcecaoQuandoVeiculoNaoEncontrado() {
        // Arrange
        Long idVeiculo = 99L;
        var command = new DesativarVeiculoCommand(idVeiculo);

        when(veiculoCommandGateway.buscarParaAlteracao(idVeiculo)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> handler.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Veiculo não encontrado.");

        verify(veiculoCommandGateway, never()).salvar(any());
    }
}