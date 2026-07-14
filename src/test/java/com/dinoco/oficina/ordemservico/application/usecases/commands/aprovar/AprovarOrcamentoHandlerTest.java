package com.dinoco.oficina.ordemservico.application.usecases.commands.aprovar;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoEventPublisher;
import com.dinoco.oficina.ordemservico.application.gateways.VerificadorEstoqueGateway;
import com.dinoco.oficina.ordemservico.domain.enums.StatusOS;
import com.dinoco.oficina.ordemservico.domain.models.ItemOSProduto;
import com.dinoco.oficina.ordemservico.domain.models.ItemOSServico;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import com.dinoco.oficina.shared.events.OrcamentoAprovadoEvent;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AprovarOrcamentoHandlerTest {

    @Mock
    private OrdemServicoCommandGateway ordemServicoCommandGateway;

    @Mock
    private OrdemServicoEventPublisher ordemServicoEventPublisher;

    @Mock
    private VerificadorEstoqueGateway verificadorEstoqueGateway;

    @InjectMocks
    private AprovarOrcamentoHandler handler;

    @Captor
    private ArgumentCaptor<OrdemServico> ordemServicoCaptor;

    @Captor
    private ArgumentCaptor<OrcamentoAprovadoEvent> eventCaptor;

    @Test
    @DisplayName("Deve aprovar orçamento com estoque disponível")
    void deveAprovarOrcamentoComEstoqueDisponivel() {
        // Arrange
        var itensProduto = new ArrayList<ItemOSProduto>();
        var quantidade = BigDecimal.valueOf(2);
        var valorUnitarioVenda = BigDecimal.valueOf(50);
        itensProduto.add(new ItemOSProduto(1L, 1L, quantidade, valorUnitarioVenda));

        var itensServico = new ArrayList<ItemOSServico>();
        var valorCobrado = BigDecimal.valueOf(300);
        itensServico.add(new ItemOSServico(1L, 1L, valorCobrado));

        var osExistente = new OrdemServico(
                1L,
                "OS-A1B2C3D4",
                1L,
                2L,
                StatusOS.AGUARDANDO_APROVACAO,
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
        osExistente.setItensProduto(itensProduto);
        osExistente.setItensServico(itensServico);

        when(ordemServicoCommandGateway.buscarParaAlteracao(1L))
                .thenReturn(Optional.of(osExistente));

        when(verificadorEstoqueGateway.todasAsPecasEstaoDisponiveis(anyList()))
                .thenReturn(true);

        var command = new AprovarOrcamentoCommand(1L);

        // Act
        handler.executar(command);

        // Assert
        verify(ordemServicoCommandGateway).buscarParaAlteracao(1L);
        verify(verificadorEstoqueGateway).todasAsPecasEstaoDisponiveis(anyList());
        verify(ordemServicoCommandGateway).salvar(ordemServicoCaptor.capture());
        verify(ordemServicoEventPublisher).publicarOrcamentoAprovado(eventCaptor.capture());

        OrdemServico osAlterada = ordemServicoCaptor.getValue();
        assertThat(osAlterada.getStatus()).isEqualTo(StatusOS.AGUARDANDO_EXECUCAO);

        OrcamentoAprovadoEvent event = eventCaptor.getValue();
        assertThat(event.osId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve aprovar orçamento aguardando fornecedor quando não há estoque")
    void deveAprovarOrcamentoAguardandoFornecedor() {
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
                StatusOS.AGUARDANDO_APROVACAO,
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
        osExistente.setItensProduto(itensProduto);
        osExistente.setItensServico(itensServico);

        when(ordemServicoCommandGateway.buscarParaAlteracao(1L))
                .thenReturn(Optional.of(osExistente));

        when(verificadorEstoqueGateway.todasAsPecasEstaoDisponiveis(anyList()))
                .thenReturn(false);

        var command = new AprovarOrcamentoCommand(1L);

        // Act
        handler.executar(command);

        // Assert
        verify(ordemServicoCommandGateway).buscarParaAlteracao(1L);
        verify(ordemServicoCommandGateway).salvar(ordemServicoCaptor.capture());
        verify(ordemServicoEventPublisher).publicarOrcamentoAprovado(any(OrcamentoAprovadoEvent.class));

        OrdemServico osAlterada = ordemServicoCaptor.getValue();
        assertThat(osAlterada.getStatus()).isEqualTo(StatusOS.AGUARDANDO_FORNECEDOR);
    }


}

