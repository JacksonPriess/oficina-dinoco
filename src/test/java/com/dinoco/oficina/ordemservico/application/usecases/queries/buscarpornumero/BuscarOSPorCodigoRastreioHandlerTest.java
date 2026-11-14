package com.dinoco.oficina.ordemservico.application.usecases.queries.buscarpornumero;

import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoQueryGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarOSPorCodigoRastreioHandlerTest {

    @Mock
    private OrdemServicoQueryGateway ordemServicoQueryGateway;

    @InjectMocks
    private BuscarOSPorCodigoRastreioHandler handler;

    @Test
    void deveRetornarOrdemServicoEncontrada() {
        var query = new BuscarOSPorCodigoRastreioQuery("OS-A1B2C3D4", 2L);
        var output = new BuscarOSPorCodigoRastreioOuput(
                1L, "OS-A1B2C3D4", 2L, "Cliente", 3L, "ABC-1234",
                "Barulho no motor", 85000, "Laudo", BigDecimal.TEN,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.TEN, "RECEBIDA"
        );
        when(ordemServicoQueryGateway.buscarPorCodigoRastreio("OS-A1B2C3D4", 2L))
                .thenReturn(Optional.of(output));

        var resultado = handler.executar(query);

        assertThat(resultado).isEqualTo(output);
        verify(ordemServicoQueryGateway).buscarPorCodigoRastreio("OS-A1B2C3D4", 2L);
    }

    @Test
    void deveLancarExcecaoQuandoOrdemServicoNaoEncontrada() {
        when(ordemServicoQueryGateway.buscarPorCodigoRastreio("OS-INEXISTENTE", 2L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.executar(
                new BuscarOSPorCodigoRastreioQuery("OS-INEXISTENTE", 2L)))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Ordem de Serviço não encontrada.");
    }
}
