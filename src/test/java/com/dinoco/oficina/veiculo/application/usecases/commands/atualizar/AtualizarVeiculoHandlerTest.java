package com.dinoco.oficina.veiculo.application.usecases.commands.atualizar;

import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarVeiculoHandlerTest {

    @Mock
    private VeiculoCommandGateway veiculoCommandGateway;

    @InjectMocks
    private AtualizarVeiculoHandler handler;

    @Captor
    private ArgumentCaptor<Veiculo> veiculoCaptor;

    @Test
    @DisplayName("Deve atualizar veículo com sucesso")
    void deveAtualizarVeiculoComSucesso() {
        // Arrange
        Long idVeiculo = 1L;
        var command = new AtualizarVeiculoCommand(
                idVeiculo, "ABC1D23", "Toyota", "Corolla", 2022, 2022, "Prata", "12345678901234567", "1.8"
        );

        // Veículo como está no banco antes da atualização
        var veiculoExistente = new Veiculo(
                1L, "ABC1234", "Toyota", "Corolla", 2020, 2020, "Preto", "11111111111111111", "1.6", true, null
        );

        // Veículo como esperamos que saia do gateway após salvar
        var veiculoMockSalvo = new Veiculo(
                1L, "ABC1D23", "Toyota", "Corolla", 2022, 2022, "Prata", "12345678901234567", "1.8", true, null
        );

        when(veiculoCommandGateway.buscarParaAlteracao(idVeiculo)).thenReturn(Optional.of(veiculoExistente));
        when(veiculoCommandGateway.salvar(any(Veiculo.class))).thenReturn(veiculoMockSalvo);

        // Act
        AtualizarVeiculoOutput response = handler.executar(command);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(idVeiculo);
        assertThat(response.placa()).isEqualTo("ABC1D23");
        assertThat(response.marca()).isEqualTo("Toyota");
        assertThat(response.modelo()).isEqualTo("Corolla");
        assertThat(response.anoFabricacao()).isEqualTo(2022);
        assertThat(response.anoModelo()).isEqualTo(2022);
        assertThat(response.cor()).isEqualTo("Prata");
        assertThat(response.chassi()).isEqualTo("12345678901234567");
        assertThat(response.motor()).isEqualTo("1.8");

        // Verificamos o objeto de domínio enviado para o gateway de salvamento
        verify(veiculoCommandGateway).salvar(veiculoCaptor.capture());
        Veiculo veiculoEnviadoParaSalvar = veiculoCaptor.getValue();

        assertThat(veiculoEnviadoParaSalvar.getPlaca()).isEqualTo("ABC1D23");
        assertThat(veiculoEnviadoParaSalvar.getMarca()).isEqualTo("Toyota");
        assertThat(veiculoEnviadoParaSalvar.getModelo()).isEqualTo("Corolla");
        assertThat(veiculoEnviadoParaSalvar.getAnoFabricacao()).isEqualTo(2022);
        assertThat(veiculoEnviadoParaSalvar.getAnoModelo()).isEqualTo(2022);
        assertThat(veiculoEnviadoParaSalvar.getCor()).isEqualTo("Prata");
        assertThat(veiculoEnviadoParaSalvar.getChassi()).isEqualTo("12345678901234567");
        assertThat(veiculoEnviadoParaSalvar.getMotor()).isEqualTo("1.8");
    }

    @Test
    @DisplayName("Deve lançar exceção quando o veículo não for encontrado")
    void deveLancarExcecaoQuandoVeiculoNaoEncontrado() {
        // Arrange
        var command = new AtualizarVeiculoCommand(
                99L, "ABC1234", "Marca", "Modelo", 2020, 2020, "Cor", "chassi", "motor"
        );

        when(veiculoCommandGateway.buscarParaAlteracao(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> handler.executar(command))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Veiculo não encontrado.");

        verify(veiculoCommandGateway, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar com placa inválida")
    void deveLancarExcecaoAoTentarAtualizarComPlacaInvalida() {
        // Arrange
        Long idVeiculo = 1L;
        // placa inválida (formato incorreto)
        var command = new AtualizarVeiculoCommand(
                idVeiculo, "INVALID", "Toyota", "Corolla", 2022, 2022, "Prata", "12345678901234567", "1.8"
        );

        var veiculoExistente = new Veiculo(
                idVeiculo, "ABC1234", "Toyota", "Corolla", 2020, 2020, "Preto", "11111111111111111", "1.6", true, null
        );

        when(veiculoCommandGateway.buscarParaAlteracao(idVeiculo)).thenReturn(Optional.of(veiculoExistente));

        // Act & Assert
        assertThatThrownBy(() -> handler.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Placa inválida. Digite apenas letras e números no padrão antigo (ABC1234) ou mercosul (ABC1D23), sem hífen.");

        verify(veiculoCommandGateway, never()).salvar(any());
    }
}
