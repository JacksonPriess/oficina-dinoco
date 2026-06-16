package com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemservico;

import com.dinoco.oficina.ordemservico.application.gateways.FuncionarioGateway;
import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.domain.enums.StatusOS;
import com.dinoco.oficina.ordemservico.domain.models.ItemOSServico;
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
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlterarItemServicoHandlerTest {

    @Mock
    private OrdemServicoCommandGateway ordemServicoCommandGateway;

    @Mock
    private FuncionarioGateway funcionarioGateway;

    @InjectMocks
    private AlterarItemServicoHandler handler;

    @Captor
    private ArgumentCaptor<OrdemServico> ordemServicoCaptor;

    @Test
    @DisplayName("Deve alterar item de serviço com sucesso")
    void deveAlterarItemServicoComSucesso() {
        // Arrange
        var itemServico = new ItemOSServico(1L, 1L, BigDecimal.TEN);
        itemServico.setId(1L);

        var itensServico = new ArrayList<ItemOSServico>();
        itensServico.add(itemServico);

        var osExistente = new OrdemServico(
                1L,
                "OS-A1B2C3D4",
                1L,
                2L,
                StatusOS.EM_DIAGNOSTICO,
                "Barulho no motor",
                null,
                85000,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                java.time.LocalDateTime.now()
        );
        osExistente.setItensServico(itensServico);
        osExistente.setItensProduto(new ArrayList<>());

        when(ordemServicoCommandGateway.buscarParaAlteracao(1L))
                .thenReturn(Optional.of(osExistente));
        when(funcionarioGateway.existeMecanicoAtivo(2L))
                .thenReturn(true);

        var command = new AlterarItemServicoCommand(1L, 1L, BigDecimal.valueOf(150), 2L);

        // Act
        handler.executar(command);

        // Assert
        verify(ordemServicoCommandGateway).buscarParaAlteracao(1L);
        verify(funcionarioGateway).existeMecanicoAtivo(2L);
        verify(ordemServicoCommandGateway).salvar(ordemServicoCaptor.capture());

        OrdemServico osAlterada = ordemServicoCaptor.getValue();
        assertThat(osAlterada.getItensServico()).hasSize(1);
        assertThat(osAlterada.getItensServico().get(0).getValorCobrado()).isEqualTo(BigDecimal.valueOf(150));
        assertThat(osAlterada.getItensServico().get(0).getMecanicoId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando mecânico não existe")
    void deveLancarExcecaoQuandoMecanicoNaoExiste() {
        // Arrange
        var osExistente = new OrdemServico(
                1L,
                "OS-A1B2C3D4",
                1L,
                2L,
                StatusOS.EM_DIAGNOSTICO,
                "Barulho no motor",
                null,
                85000,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                java.time.LocalDateTime.now()
        );

        when(ordemServicoCommandGateway.buscarParaAlteracao(1L))
                .thenReturn(Optional.of(osExistente));
        when(funcionarioGateway.existeMecanicoAtivo(anyLong()))
                .thenReturn(false);

        var command = new AlterarItemServicoCommand(1L, 1L, BigDecimal.valueOf(150), 999L);

        // Act & Assert
        assertThatThrownBy(() -> handler.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mecânico não encontrado ou inativo.");
    }
}

