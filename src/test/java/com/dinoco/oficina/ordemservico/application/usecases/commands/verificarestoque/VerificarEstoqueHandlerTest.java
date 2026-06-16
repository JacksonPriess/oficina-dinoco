package com.dinoco.oficina.ordemservico.application.usecases.commands.verificarestoque;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.application.gateways.VerificadorEstoqueGateway;
import com.dinoco.oficina.ordemservico.domain.enums.StatusOS;
import com.dinoco.oficina.ordemservico.domain.exceptions.RecursoNaoEncontradoException;
import com.dinoco.oficina.ordemservico.domain.exceptions.RegraNegocioOSException;
import com.dinoco.oficina.ordemservico.domain.models.ItemOSProduto;
import com.dinoco.oficina.ordemservico.domain.models.ItemOSServico;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import com.dinoco.oficina.ordemservico.infrastructure.web.dto.PecaPendenteDto;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificarEstoqueHandlerTest {

    @Mock
    private OrdemServicoCommandGateway ordemServicoCommandGateway;

    @Mock
    private VerificadorEstoqueGateway verificadorEstoqueGateway;

    @InjectMocks
    private VerificarEstoqueHandler handler;

    @Captor
    private ArgumentCaptor<OrdemServico> ordemServicoCaptor;

    @Test
    @DisplayName("Deve verificar estoque com todas as peças disponíveis")
    void deveVerificarEstoqueComTodasPecasDisponiveis() {
        // Arrange
        var itensProduto = new ArrayList<ItemOSProduto>();
        itensProduto.add(new ItemOSProduto(1L, 1L, BigDecimal.valueOf(2), BigDecimal.valueOf(50)));

        var itensServico = new ArrayList<ItemOSServico>();
        var valorCobrado = BigDecimal.valueOf(300);
        itensServico.add(new ItemOSServico(1L, 1L, valorCobrado));

        var osExistente = new OrdemServico(
                1L,
                "OS-A1B2C3D4",
                1L,
                2L,
                StatusOS.AGUARDANDO_FORNECEDOR,
                "Barulho no motor",
                "Laudo técnico",
                85000,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.valueOf(100),
                java.math.BigDecimal.valueOf(100),
                java.math.BigDecimal.valueOf(200),
                java.time.LocalDateTime.now()
        );
        osExistente.setItensProduto(itensProduto);
        osExistente.setItensServico(itensServico);

        when(ordemServicoCommandGateway.buscarParaAlteracao(1L))
                .thenReturn(Optional.of(osExistente));

        when(verificadorEstoqueGateway.buscarPecasComEstoqueInsuficiente(anyList()))
                .thenReturn(new ArrayList<>());

        var command = new VerificarEstoqueCommand(1L);

        // Act
        VerificarEstoqueOutput response = handler.executar(command);

        // Assert - Que ocorreu entrada no estoque
        assertThat(response).isNotNull();
        assertThat(response.osId()).isEqualTo(1L);
        assertThat(response.prontaParaExecucao()).isTrue();
        assertThat(response.pecasFaltantes()).isEmpty();

        verify(ordemServicoCommandGateway).buscarParaAlteracao(1L);
        verify(verificadorEstoqueGateway).buscarPecasComEstoqueInsuficiente(anyList());
        verify(ordemServicoCommandGateway).salvar(ordemServicoCaptor.capture());

        OrdemServico osAlterada = ordemServicoCaptor.getValue();
        assertThat(osAlterada.getStatus()).isEqualTo(StatusOS.AGUARDANDO_EXECUCAO);
    }

    @Test
    @DisplayName("Deve verificar estoque com peças pendentes")
    void deveVerificarEstoqueComPecasPendentes() {
        // Arrange
        var itensProduto = new ArrayList<ItemOSProduto>();
        itensProduto.add(new ItemOSProduto(1L, 1L, BigDecimal.valueOf(2), BigDecimal.valueOf(50)));

        var osExistente = new OrdemServico(
                1L,
                "OS-A1B2C3D4",
                1L,
                2L,
                StatusOS.AGUARDANDO_FORNECEDOR,
                "Barulho no motor",
                "Laudo técnico",
                85000,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.valueOf(100),
                java.math.BigDecimal.valueOf(100),
                java.math.BigDecimal.valueOf(200),
                java.time.LocalDateTime.now()
        );
        osExistente.setItensProduto(itensProduto);

        List<PecaPendenteDto> pecasPendentes = List.of(
                new PecaPendenteDto(1L, BigDecimal.valueOf(5))
        );

        when(ordemServicoCommandGateway.buscarParaAlteracao(1L))
                .thenReturn(Optional.of(osExistente));
        when(verificadorEstoqueGateway.buscarPecasComEstoqueInsuficiente(anyList()))
                .thenReturn(pecasPendentes);

        var command = new VerificarEstoqueCommand(1L);

        // Act
        VerificarEstoqueOutput response = handler.executar(command);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.osId()).isEqualTo(1L);
        assertThat(response.prontaParaExecucao()).isFalse();
        assertThat(response.pecasFaltantes()).hasSize(1);

        verify(ordemServicoCommandGateway).buscarParaAlteracao(1L);
        verify(verificadorEstoqueGateway).buscarPecasComEstoqueInsuficiente(anyList());
    }

    @Test
    @DisplayName("Deve lançar exceção quando OS não encontrada")
    void deveLancarExcecaoQuandoOSNaoEncontrada() {
        // Arrange
        when(ordemServicoCommandGateway.buscarParaAlteracao(anyLong()))
                .thenReturn(Optional.empty());

        var command = new VerificarEstoqueCommand(999L);

        // Act & Assert
        assertThatThrownBy(() -> handler.executar(command))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("OS não encontrada.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando OS não está em AGUARDANDO_FORNECEDOR")
    void deveLancarExcecaoQuandoOSNaoEstaAguardandoFornecedor() {
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

        var command = new VerificarEstoqueCommand(1L);

        // Act & Assert
        assertThatThrownBy(() -> handler.executar(command))
                .isInstanceOf(RegraNegocioOSException.class)
                .hasMessage("A verificação de estoque só pode ser realizada quando a OS estiver AGUARDANDO_FORNECEDOR.");
    }
}

