package com.dinoco.oficina.ordemservico.application.usecases.commands.finalizarexecucao;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.domain.enums.StatusOS;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
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
class FinalizarExecucaoHandlerTest {

    @Mock
    private OrdemServicoCommandGateway ordemServicoCommandGateway;

    @InjectMocks
    private FinalizarExecucaoHandler handler;

    @Captor
    private ArgumentCaptor<OrdemServico> ordemServicoCaptor;

    @Test
    @DisplayName("Deve finalizar execução com sucesso")
    void deveFinalizarExecucaoComSucesso() {
        // Arrange
        var itemServico = new ItemOSServico(1L, 1L, BigDecimal.TEN);
        itemServico.setId(1L);
        itemServico.iniciarExecucao();
        itemServico.concluirExecucao(null);

        var itensServico = new ArrayList<ItemOSServico>();
        itensServico.add(itemServico);

        var osExistente = new OrdemServico(
                1L,
                "OS-A1B2C3D4",
                1L,
                2L,
                StatusOS.EM_EXECUCAO,
                "Barulho no motor",
                "Laudo técnico",
                85000,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.valueOf(100),
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.valueOf(100),
                java.time.LocalDateTime.now(),
                null,
                null
        );
        osExistente.setItensServico(itensServico);

        when(ordemServicoCommandGateway.buscarParaAlteracao(1L))
                .thenReturn(Optional.of(osExistente));

        var command = new FinalizarExecucaoCommand(1L);

        // Act
        handler.executar(command);

        // Assert
        verify(ordemServicoCommandGateway).buscarParaAlteracao(1L);
        verify(ordemServicoCommandGateway).salvar(ordemServicoCaptor.capture());

        OrdemServico osAlterada = ordemServicoCaptor.getValue();
        assertThat(osAlterada.getStatus()).isEqualTo(StatusOS.FINALIZADA);
    }

    @Test
    @DisplayName("Deve lançar exceção quando OS não encontrada")
    void deveLancarExcecaoQuandoOSNaoEncontrada() {
        // Arrange
        when(ordemServicoCommandGateway.buscarParaAlteracao(anyLong()))
                .thenReturn(Optional.empty());

        var command = new FinalizarExecucaoCommand(999L);

        // Act & Assert
        assertThatThrownBy(() -> handler.executar(command))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("OS não encontrada.");
    }
}

