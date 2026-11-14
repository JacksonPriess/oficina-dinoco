package com.dinoco.oficina.ordemservico.application.usecases.commands.atualizarstatus;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluir.ConcluirOrdemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluir.ConcluirOrdemServicoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirdiagnostico.ConcluirDiagnosticoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.enviarorcamento.EnviarOrcamentoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.enviarorcamento.EnviarOrcamentoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.finalizarexecucao.FinalizarExecucaoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.finalizarexecucao.FinalizarExecucaoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciardiagnostico.IniciarDiagnosticoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucao.IniciarExecucaoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucao.IniciarExecucaoUseCase;
import com.dinoco.oficina.ordemservico.domain.enums.StatusOS;
import com.dinoco.oficina.ordemservico.domain.models.ItemOSProduto;
import com.dinoco.oficina.ordemservico.domain.models.ItemOSServico;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarStatusHandlerTest {

    @Mock
    private OrdemServicoCommandGateway ordemServicoCommandGateway;

    @Mock
    private IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase;

    @Mock
    private ConcluirDiagnosticoUseCase concluirDiagnosticoUseCase;

    @Mock
    private EnviarOrcamentoUseCase enviarOrcamentoUseCase;

    @Mock
    private IniciarExecucaoUseCase iniciarExecucaoUseCase;

    @Mock
    private FinalizarExecucaoUseCase finalizarExecucaoUseCase;

    @Mock
    private ConcluirOrdemServicoUseCase concluirOrdemServicoUseCase;

    @InjectMocks
    private AtualizarStatusHandler handler;

    @Test
    @DisplayName("Deve permitir mudar de RECEBIDA para EM_DIAGNOSTICO")
    void devePermitirIniciarDianostigo() {
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
                StatusOS.RECEBIDA,
                "Barulho no motor",
                "Laudo técnico",
                85000,
                BigDecimal.ZERO,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(200),
                java.time.LocalDateTime.now(),
                null,
                null,
                null,
                null,
                null,
                null
        );
        osExistente.setItensProduto(itensProduto);
        osExistente.setItensServico(itensServico);

        when(ordemServicoCommandGateway.buscarPorCodigoRastreioParaAlteracao("OS-A1B2C3D4"))
                .thenReturn(Optional.of(osExistente));

        var command = new AtualizarStatusCommand(
                "OS-A1B2C3D4",
                "INICIAR_DIAGNOSTICO",
                "Laudo de entrega"
        );
        handler.executar(command);

        // Assert
        verify(ordemServicoCommandGateway).buscarPorCodigoRastreioParaAlteracao("OS-A1B2C3D4");
        verify(iniciarDiagnosticoUseCase, times(1)).executar(any());

    }

    @Test
    @DisplayName("Deve permitir mudar de EM_DIAGNOSTICO para AGUARDANDO_ORCAMENTO")
    void devePermitirConcluirDianostigo() {
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
                StatusOS.EM_DIAGNOSTICO,
                "Barulho no motor",
                "Laudo técnico",
                85000,
                BigDecimal.ZERO,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(200),
                java.time.LocalDateTime.now(),
                null,
                null,
                null,
                null,
                null,
                null
        );
        osExistente.setItensProduto(itensProduto);
        osExistente.setItensServico(itensServico);

        when(ordemServicoCommandGateway.buscarPorCodigoRastreioParaAlteracao("OS-A1B2C3D4"))
                .thenReturn(Optional.of(osExistente));

        var command = new AtualizarStatusCommand(
                "OS-A1B2C3D4",
                "CONCLUIR_DIAGNOSTICO",
                "Laudo de entrega"
        );
        handler.executar(command);

        // Assert
        verify(ordemServicoCommandGateway).buscarPorCodigoRastreioParaAlteracao("OS-A1B2C3D4");
        verify(concluirDiagnosticoUseCase, times(1)).executar(any());

    }

    @Test
    @DisplayName("Deve permitir enviar orçamento")
    void devePermitirEnviarOrcamento() {
        when(ordemServicoCommandGateway.buscarPorCodigoRastreioParaAlteracao("OS-A1B2C3D4"))
                .thenReturn(Optional.of(novaOrdemServico()));

        handler.executar(new AtualizarStatusCommand("OS-A1B2C3D4", "ENVIAR_ORCAMENTO", null));

        verify(enviarOrcamentoUseCase).executar(new EnviarOrcamentoCommand(1L));
        verifyNoInteractions(iniciarDiagnosticoUseCase, concluirDiagnosticoUseCase,
                iniciarExecucaoUseCase, finalizarExecucaoUseCase, concluirOrdemServicoUseCase);
    }

    @Test
    @DisplayName("Deve permitir iniciar execução")
    void devePermitirIniciarExecucao() {
        when(ordemServicoCommandGateway.buscarPorCodigoRastreioParaAlteracao("OS-A1B2C3D4"))
                .thenReturn(Optional.of(novaOrdemServico()));

        handler.executar(new AtualizarStatusCommand("OS-A1B2C3D4", "INICIAR_EXECUCAO", null));

        verify(iniciarExecucaoUseCase).executar(new IniciarExecucaoCommand(1L));
        verifyNoInteractions(iniciarDiagnosticoUseCase, concluirDiagnosticoUseCase,
                enviarOrcamentoUseCase, finalizarExecucaoUseCase, concluirOrdemServicoUseCase);
    }

    @Test
    @DisplayName("Deve permitir finalizar execução")
    void devePermitirFinalizarExecucao() {
        when(ordemServicoCommandGateway.buscarPorCodigoRastreioParaAlteracao("OS-A1B2C3D4"))
                .thenReturn(Optional.of(novaOrdemServico()));

        handler.executar(new AtualizarStatusCommand("OS-A1B2C3D4", "FINALIZAR_EXECUCAO", null));

        verify(finalizarExecucaoUseCase).executar(new FinalizarExecucaoCommand(1L));
        verifyNoInteractions(iniciarDiagnosticoUseCase, concluirDiagnosticoUseCase,
                enviarOrcamentoUseCase, iniciarExecucaoUseCase, concluirOrdemServicoUseCase);
    }

    @Test
    @DisplayName("Deve permitir concluir ordem de serviço")
    void devePermitirConcluirOrdemServico() {
        when(ordemServicoCommandGateway.buscarPorCodigoRastreioParaAlteracao("OS-A1B2C3D4"))
                .thenReturn(Optional.of(novaOrdemServico()));

        handler.executar(new AtualizarStatusCommand("OS-A1B2C3D4", "CONCLUIR", null));

        verify(concluirOrdemServicoUseCase).executar(new ConcluirOrdemServicoCommand(1L));
        verifyNoInteractions(iniciarDiagnosticoUseCase, concluirDiagnosticoUseCase,
                enviarOrcamentoUseCase, iniciarExecucaoUseCase, finalizarExecucaoUseCase);
    }

    @Test
    @DisplayName("Deve lançar exceção quando OS não encontrada")
    void deveLancarExcecaoQuandoOSNaoEncontrada() {
        when(ordemServicoCommandGateway.buscarPorCodigoRastreioParaAlteracao("OS-INEXISTENTE"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.executar(
                new AtualizarStatusCommand("OS-INEXISTENTE", "INICIAR_DIAGNOSTICO", null)))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("OS não encontrada para o código de rastreio: OS-INEXISTENTE");

        verifyNoInteractions(iniciarDiagnosticoUseCase, concluirDiagnosticoUseCase,
                enviarOrcamentoUseCase, iniciarExecucaoUseCase, finalizarExecucaoUseCase,
                concluirOrdemServicoUseCase);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a ação não é suportada")
    void deveLancarExcecaoQuandoAcaoNaoSuportada() {
        when(ordemServicoCommandGateway.buscarPorCodigoRastreioParaAlteracao("OS-A1B2C3D4"))
                .thenReturn(Optional.of(novaOrdemServico()));

        assertThatThrownBy(() -> handler.executar(
                new AtualizarStatusCommand("OS-A1B2C3D4", "ACAO_INVALIDA", null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ação de webhook não suportada: ACAO_INVALIDA");

        verifyNoInteractions(iniciarDiagnosticoUseCase, concluirDiagnosticoUseCase,
                enviarOrcamentoUseCase, iniciarExecucaoUseCase, finalizarExecucaoUseCase,
                concluirOrdemServicoUseCase);
    }

    private OrdemServico novaOrdemServico() {
        return new OrdemServico(
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
                java.time.LocalDateTime.now(),
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}