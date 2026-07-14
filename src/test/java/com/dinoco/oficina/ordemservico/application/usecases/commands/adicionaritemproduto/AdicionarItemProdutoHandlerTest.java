package com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemproduto;

import com.dinoco.oficina.ordemservico.application.gateways.CatalogoProdutoGateway;
import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.domain.enums.StatusOS;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdicionarItemProdutoHandlerTest {

    @Mock
    private OrdemServicoCommandGateway ordemServicoCommandGateway;

    @Mock
    private CatalogoProdutoGateway catalogoProdutoGateway;

    @InjectMocks
    private AdicionarItemProdutoHandler handler;

    @Captor
    private ArgumentCaptor<OrdemServico> ordemServicoCaptor;

    @Test
    @DisplayName("Deve adicionar produto com sucesso")
    void deveAdicionarProdutoComSucesso() {
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
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                LocalDateTime.now(),
                null,
                null
        );
        osExistente.setItensProduto(new java.util.ArrayList<>());
        osExistente.setItensServico(new java.util.ArrayList<>());

        when(ordemServicoCommandGateway.buscarParaAlteracao(1L))
                .thenReturn(Optional.of(osExistente));

        when(catalogoProdutoGateway.buscarPrecoVendaAtual(1L))
                .thenReturn(BigDecimal.valueOf(50));

        var command = new AdicionarItemProdutoCommand(1L, 1L, BigDecimal.valueOf(2));

        // Act
        handler.executar(command);

        // Assert
        verify(ordemServicoCommandGateway).buscarParaAlteracao(1L);
        verify(catalogoProdutoGateway).buscarPrecoVendaAtual(1L);
        verify(ordemServicoCommandGateway).salvar(ordemServicoCaptor.capture());

        OrdemServico osAlterada = ordemServicoCaptor.getValue();
        assertThat(osAlterada.getItensProduto()).hasSize(1);
        assertThat(osAlterada.getItensProduto().get(0).getProdutoId()).isEqualTo(1L);
        assertThat(osAlterada.getItensProduto().get(0).getQuantidade()).isEqualTo(BigDecimal.valueOf(2));
    }

    @Test
    @DisplayName("Deve lançar exceção quando OS não encontrada")
    void deveLancarExcecaoQuandoOSNaoEncontrada() {
        // Arrange
        when(ordemServicoCommandGateway.buscarParaAlteracao(anyLong()))
                .thenReturn(Optional.empty());

        var command = new AdicionarItemProdutoCommand(999L, 1L, BigDecimal.ONE);

        // Act & Assert
        assertThatThrownBy(() -> handler.executar(command))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("OS não encontrada.");
    }
}