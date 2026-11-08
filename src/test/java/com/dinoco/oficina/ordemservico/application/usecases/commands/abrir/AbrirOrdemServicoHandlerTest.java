package com.dinoco.oficina.ordemservico.application.usecases.commands.abrir;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.application.gateways.CatalogoServicoGateway;
import com.dinoco.oficina.ordemservico.domain.enums.StatusOS;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbrirOrdemServicoHandlerTest {

    @Mock
    private OrdemServicoCommandGateway ordemServicoCommandGateway;

    @Mock
    private CatalogoServicoGateway catalogoServicoGateway;

    @InjectMocks
    private AbrirOrdemServicoHandler handler;

    @Captor
    private ArgumentCaptor<OrdemServico> ordemServicoCaptor;

    @Test
    @DisplayName("Deve abrir ordem de serviço com sucesso")
    void deveAbrirOrdemServicoComSucesso() {
        // Arrange
        var command = new AbrirOrdemServicoCommand(
                1L,
                2L,
                85000,
                "Barulho no motor",
                null,
                null
        );

        var ordemServicoMockSalva = new OrdemServico(
                1L,
                "OS-A1B2C3D4",
                1L,
                2L,
                StatusOS.RECEBIDA,
                "Barulho no motor",
                null,
                85000,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                LocalDateTime.now(),
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(ordemServicoCommandGateway.salvar(any(OrdemServico.class)))
                .thenReturn(ordemServicoMockSalva);

        // Act
        AbrirOrdemServicoOutput response = handler.executar(command);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.osId()).isEqualTo(1L);
        assertThat(response.codigoRastreio()).isEqualTo("OS-A1B2C3D4");
        assertThat(response.clienteId()).isEqualTo(1L);
        assertThat(response.veiculoId()).isEqualTo(2L);
        assertThat(response.status()).isEqualTo(StatusOS.RECEBIDA);
        assertThat(response.reclamacaoCliente()).isEqualTo("Barulho no motor");
        assertThat(response.quilometragemEntrada()).isEqualTo(85000);

        verify(ordemServicoCommandGateway).salvar(ordemServicoCaptor.capture());
        OrdemServico ordemServicoSalva = ordemServicoCaptor.getValue();

        assertThat(ordemServicoSalva.getClienteId()).isEqualTo(1L);
        assertThat(ordemServicoSalva.getVeiculoId()).isEqualTo(2L);
        assertThat(ordemServicoSalva.getQuilometragemEntrada()).isEqualTo(85000);
        assertThat(ordemServicoSalva.getReclamacaoCliente()).isEqualTo("Barulho no motor");
        assertThat(ordemServicoSalva.getStatus()).isEqualTo(StatusOS.RECEBIDA);
    }

    @Test
    void osExistentePreservaValorQuandoPrecoDoCatalogoMuda() {
        var command = new AbrirOrdemServicoCommand(1L, 2L, 85000, "Manutenção", null,
                List.of(new AbrirOrdemServicoItemServicoCommand(9L, null)));
        when(catalogoServicoGateway.buscarPrecoPadrao(9L))
                .thenReturn(Optional.of(new BigDecimal("100.00")), Optional.of(new BigDecimal("120.00")));
        when(ordemServicoCommandGateway.salvar(any(OrdemServico.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        handler.executar(command);
        handler.executar(command);

        verify(ordemServicoCommandGateway, org.mockito.Mockito.times(2)).salvar(ordemServicoCaptor.capture());
        var ordens = ordemServicoCaptor.getAllValues();
        assertThat(ordens.get(0).getItensServico().getFirst().getValorCobrado()).isEqualByComparingTo("100.00");
        assertThat(ordens.get(1).getItensServico().getFirst().getValorCobrado()).isEqualByComparingTo("120.00");
    }
}
