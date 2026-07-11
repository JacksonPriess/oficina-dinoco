package com.dinoco.oficina.ordemservico.application.usecases.commands.enviarorcamento;

import com.dinoco.oficina.ordemservico.application.gateways.ClienteContatoGateway;
import com.dinoco.oficina.ordemservico.application.gateways.ClienteContatoOutput;
import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.domain.enums.StatusOS;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.ordemservico.domain.models.ItemOSServico;
import com.dinoco.oficina.ordemservico.domain.models.ItemOSProduto;
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
class EnviarOrcamentoHandlerTest {

    @Mock
    private OrdemServicoCommandGateway ordemServicoCommandGateway;

    @Mock
    private ClienteContatoGateway clienteContatoGateway;

    @InjectMocks
    private EnviarOrcamentoHandler handler;

    @Captor
    private ArgumentCaptor<OrdemServico> ordemServicoCaptor;

    @Test
    @DisplayName("Deve enviar orçamento com sucesso")
    void deveEnviarOrcamentoComSucesso() {
        // Arrange
        var itensServico = new ArrayList<ItemOSServico>();
        itensServico.add(new ItemOSServico(1L, 1L, BigDecimal.valueOf(100)));

        var itensProduto = new ArrayList<ItemOSProduto>();
        itensProduto.add(new ItemOSProduto(1L, 1L, BigDecimal.valueOf(2), BigDecimal.valueOf(50)));

        var osExistente = new OrdemServico(
                1L,
                "OS-A1B2C3D4",
                1L,
                2L,
                StatusOS.AGUARDANDO_ORCAMENTO,
                "Barulho no motor",
                "Laudo técnico",
                85000,
                BigDecimal.ZERO,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(200),
                java.time.LocalDateTime.now(),
                null,
                null
        );
        osExistente.setItensServico(itensServico);
        osExistente.setItensProduto(itensProduto);

        when(ordemServicoCommandGateway.buscarParaAlteracao(1L))
                .thenReturn(Optional.of(osExistente));

        when(clienteContatoGateway.buscarContato(1L))
                .thenReturn(new ClienteContatoOutput("João Silva", "11987654321"));

        var command = new EnviarOrcamentoCommand(1L);

        // Act
        EnviarOrcamentoOutput response = handler.executar(command);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.linkWhatsApp()).contains("wa.me");
        assertThat(response.linkWhatsApp()).contains("55");

        verify(ordemServicoCommandGateway).buscarParaAlteracao(1L);
        verify(clienteContatoGateway).buscarContato(1L);
        verify(ordemServicoCommandGateway).salvar(ordemServicoCaptor.capture());

        OrdemServico osAlterada = ordemServicoCaptor.getValue();
        assertThat(osAlterada.getStatus()).isEqualTo(StatusOS.AGUARDANDO_APROVACAO);
    }

    @Test
    @DisplayName("Deve lançar exceção quando OS não encontrada")
    void deveLancarExcecaoQuandoOSNaoEncontrada() {
        // Arrange
        when(ordemServicoCommandGateway.buscarParaAlteracao(anyLong()))
                .thenReturn(Optional.empty());

        var command = new EnviarOrcamentoCommand(999L);

        // Act & Assert
        assertThatThrownBy(() -> handler.executar(command))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("OS não encontrada.");
    }
}


