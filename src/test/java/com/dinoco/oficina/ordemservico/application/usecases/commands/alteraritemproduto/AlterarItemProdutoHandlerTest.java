package com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemproduto;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.domain.enums.StatusOS;
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
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlterarItemProdutoHandlerTest {

    @Mock
    private OrdemServicoCommandGateway ordemServicoCommandGateway;

    @InjectMocks
    private AlterarItemProdutoHandler handler;

    @Captor
    private ArgumentCaptor<OrdemServico> ordemServicoCaptor;

    @Test
    @DisplayName("Deve alterar item de produto com sucesso")
    void deveAlterarItemProdutoComSucesso() {
        // Arrange
        var itemProduto = new ItemOSProduto(1L, 1L, BigDecimal.valueOf(2), BigDecimal.valueOf(50));

        var itensProduto = new ArrayList<ItemOSProduto>();
        itensProduto.add(itemProduto);

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
        osExistente.setItensProduto(itensProduto);
        osExistente.setItensServico(new ArrayList<>());

        when(ordemServicoCommandGateway.buscarParaAlteracao(1L))
                .thenReturn(Optional.of(osExistente));

        var command = new AlterarItemProdutoCommand(1L, 1L, BigDecimal.valueOf(75), BigDecimal.valueOf(3));

        // Act
        handler.executar(command);

        // Assert
        verify(ordemServicoCommandGateway).buscarParaAlteracao(1L);
        verify(ordemServicoCommandGateway).salvar(ordemServicoCaptor.capture());

        OrdemServico osAlterada = ordemServicoCaptor.getValue();
        assertThat(osAlterada.getItensProduto()).hasSize(1);
        assertThat(osAlterada.getItensProduto().get(0).getValorUnitarioVenda()).isEqualTo(BigDecimal.valueOf(75));
        assertThat(osAlterada.getItensProduto().get(0).getQuantidade()).isEqualTo(BigDecimal.valueOf(3));
    }

    @Test
    @DisplayName("Deve lançar exceção quando OS não encontrada")
    void deveLancarExcecaoQuandoOSNaoEncontrada() {
        // Arrange
        when(ordemServicoCommandGateway.buscarParaAlteracao(999L))
                .thenReturn(Optional.empty());

        var command = new AlterarItemProdutoCommand(999L, 1L, BigDecimal.valueOf(75), BigDecimal.valueOf(3));

        // Act & Assert
        assertThatThrownBy(() -> handler.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("OS não encontrada.");
    }
}

