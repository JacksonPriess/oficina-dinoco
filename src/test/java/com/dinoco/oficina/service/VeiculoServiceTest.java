package com.dinoco.oficina.service;

import com.dinoco.oficina.dto.VeiculoResponseDto;
import com.dinoco.oficina.entity.Veiculo;
import com.dinoco.oficina.util.builders.VeiculoBuilder;
import com.dinoco.oficina.util.builders.VeiculoRequestDtoBuilder;
import com.dinoco.oficina.repository.VeiculoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    @InjectMocks
    private VeiculoService veiculoService;

    @Mock
    private VeiculoRepository repository;

    @Captor
    private ArgumentCaptor<Veiculo> veiculoCaptor;

    @Test
    @DisplayName("Deve criar veículo com sucesso quando placa não existir")
    void deveCriarVeiculoComSucesso() {
        // 1. Arrange
        var requestDto = VeiculoRequestDtoBuilder.umRequest().build();
        var veiculoSalvoMock = VeiculoBuilder.umVeiculo().build();
        when(repository.existsByPlaca(requestDto.placa())).thenReturn(false);
        when(repository.save(any(Veiculo.class))).thenReturn(veiculoSalvoMock);
        // 2. Act
        VeiculoResponseDto response = veiculoService.criar(requestDto);
        // 3. Assert
        assertNotNull(response);
        assertEquals(veiculoSalvoMock.getId(), response.id());
        verify(repository, times(1)).existsByPlaca(requestDto.placa());
        verify(repository, times(1)).save(veiculoCaptor.capture());
        Veiculo entidadeCapturada = veiculoCaptor.getValue();
        assertEquals(requestDto.placa(), entidadeCapturada.getPlaca());
        assertEquals(requestDto.marca(), entidadeCapturada.getMarca());
        assertEquals(requestDto.modelo(), entidadeCapturada.getModelo());
        assertEquals(requestDto.anoFabricacao(), entidadeCapturada.getAnoFabricacao());
        assertEquals(requestDto.anoModelo(), entidadeCapturada.getAnoModelo());
        assertEquals(requestDto.cor(), entidadeCapturada.getCor());
        assertEquals(requestDto.chassi(), entidadeCapturada.getChassi());
        assertEquals(requestDto.motor(), entidadeCapturada.getMotor());
    }

    @Test
    @DisplayName("Não deve criar veículo e deve lançar exceção quando placa já estiver cadastrada")
    void naoDeveCriarVeiculoQuandoPlacaJaExiste() {
        // 1. Arrange
        var requestDto = VeiculoRequestDtoBuilder.umRequest().build();
        when(repository.existsByPlaca(requestDto.placa())).thenReturn(true);

        // 2. Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> veiculoService.criar(requestDto));

        assertEquals("Veículo já cadastrado com esta placa.", exception.getMessage());
        verify(repository, times(1)).existsByPlaca(requestDto.placa());
        verify(repository, never()).save(any(Veiculo.class));
    }
}