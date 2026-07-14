package com.dinoco.oficina.veiculo.application.usecases.commands.criar;

import com.dinoco.oficina.veiculo.application.gateways.VeiculoCommandGateway;
import com.dinoco.oficina.veiculo.application.gateways.VeiculoQueryGateway;
import com.dinoco.oficina.veiculo.domain.Veiculo;
import org.junit.jupiter.api.DisplayName;
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

@ExtendWith(MockitoExtension.class)
class CriarVeiculoHandlerTest {

    @Mock
    private VeiculoCommandGateway commandGateway;

    @Mock
    private VeiculoQueryGateway queryGateway;

    @InjectMocks
    private CriarVeiculoHandler handler;

    @Captor
    private ArgumentCaptor<Veiculo> veiculoCaptor;

    @Test
    @DisplayName("Deve criar veículo com sucesso")
    void deveCriarVeiculoComSucesso() {
        // Arrange
        var command = new CriarVeiculoCommand(
                "ABC1234", "Toyota", "Corolla", 2023, 2021, "Prata", "12345678901234567", "1.8"
        );

        var veiculoMockSalvo = new Veiculo(
                1L, "ABC1234", "Toyota", "Corolla", 2023, 2021, "Prata", "12345678901234567", "1.8", true, null
        );

        when(queryGateway.existePorPlaca(anyString())).thenReturn(false);
        when(commandGateway.salvar(any(Veiculo.class))).thenReturn(veiculoMockSalvo);

        // Act
        CriarVeiculoOutput response = handler.executar(command);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.placa()).isEqualTo("ABC1234");

        verify(commandGateway).salvar(veiculoCaptor.capture());
        Veiculo veiculoSalvo = veiculoCaptor.getValue();
        
        assertThat(veiculoSalvo.getPlaca()).isEqualTo("ABC1234");
        assertThat(veiculoSalvo.getMarca()).isEqualTo("Toyota");
        assertThat(veiculoSalvo.getModelo()).isEqualTo("Corolla");
        assertThat(veiculoSalvo.getAnoFabricacao()).isEqualTo(2023);
        assertThat(veiculoSalvo.getAnoModelo()).isEqualTo(2021);
        assertThat(veiculoSalvo.getCor()).isEqualTo("Prata");
        assertThat(veiculoSalvo.getChassi()).isEqualTo("12345678901234567");
        assertThat(veiculoSalvo.getMotor()).isEqualTo("1.8");
        assertThat(veiculoSalvo.getAtivo()).isTrue();
    }
}
