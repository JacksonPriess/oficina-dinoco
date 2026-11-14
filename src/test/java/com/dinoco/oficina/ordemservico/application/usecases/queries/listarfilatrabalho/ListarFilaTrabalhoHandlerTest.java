package com.dinoco.oficina.ordemservico.application.usecases.queries.listarfilatrabalho;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoQueryGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarFilaTrabalhoHandlerTest {

    @Mock
    private OrdemServicoQueryGateway ordemServicoQueryGateway;

    @InjectMocks
    private ListarFilaTrabalhoHandler handler;

    @Test
    void deveRetornarFilaDeTrabalho() {
        var ordemServico = new ListarFilaTrabalhoDetalhesOutput(
                1L, "OS-A1B2C3D4", 2L, 3L, "EM_EXECUCAO",
                LocalDateTime.now(), BigDecimal.valueOf(150)
        );
        when(ordemServicoQueryGateway.listarFilaDeTrabalho()).thenReturn(List.of(ordemServico));

        var resultado = handler.executar(new ListarFilaTrabalhoQuery());

        assertThat(resultado.dataConsulta()).isNotNull();
        assertThat(resultado.ordensServico()).containsExactly(ordemServico);
        verify(ordemServicoQueryGateway).listarFilaDeTrabalho();
    }
}