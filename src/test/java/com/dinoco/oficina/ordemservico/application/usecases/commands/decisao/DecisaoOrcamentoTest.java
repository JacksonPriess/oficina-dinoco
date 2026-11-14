package com.dinoco.oficina.ordemservico.application.usecases.commands.decisao;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.application.usecases.commands.aprovar.*;
import com.dinoco.oficina.ordemservico.application.usecases.commands.reprovar.*;
import com.dinoco.oficina.ordemservico.application.usecases.commands.decisaoclienteautenticado.*;
import com.dinoco.oficina.ordemservico.application.usecases.commands.decisaoclienteorcamento.*;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class DecisaoOrcamentoTest {
    private final OrdemServicoCommandGateway gateway = mock(OrdemServicoCommandGateway.class);
    private final AprovarOrcamentoUseCase aprovar = mock(AprovarOrcamentoUseCase.class);
    private final ReprovarOrcamentoUseCase reprovar = mock(ReprovarOrcamentoUseCase.class);
    private final DecisaoClienteHandler webhook = new DecisaoClienteHandler(gateway, aprovar, reprovar);
    private final DecisaoClienteAutenticadoHandler cliente = new DecisaoClienteAutenticadoHandler(gateway, aprovar, reprovar);

    @ParameterizedTest @ValueSource(strings = {"aprovado", "RECUSADO"})
    void encaminharDecisaoSemDuplicarRegraDeNegocio(String decisao) {
        var os = mock(OrdemServico.class);
        when(os.getId()).thenReturn(9L);
        when(gateway.buscarPorCodigoRastreioParaAlteracao("OS-9")).thenReturn(Optional.of(os));
        when(gateway.buscarPorCodigoRastreioEClienteParaAlteracao("OS-9", 7L)).thenReturn(Optional.of(os));
        webhook.executar(new DecisaoClienteCommand("OS-9", decisao, "Observação"));
        cliente.executar(new DecisaoClienteAutenticadoCommand("OS-9", 7L, decisao, "Observação"));
        if (decisao.equalsIgnoreCase("APROVADO")) {
            verify(aprovar, times(2)).executar(new AprovarOrcamentoCommand(9L));
            verifyNoInteractions(reprovar);
        } else {
            verify(reprovar, times(2)).executar(new ReprovarOrcamentoCommand(9L));
            verifyNoInteractions(aprovar);
        }
        verify(gateway, never()).salvar(any());
    }
    @Test void clienteNaoPodeDecidirOSDeOutraPessoa() {
        assertThatThrownBy(() -> cliente.executar(new DecisaoClienteAutenticadoCommand("OS-9", 8L, "APROVADO", null)))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        verify(gateway).buscarPorCodigoRastreioEClienteParaAlteracao("OS-9", 8L);
        verifyNoInteractions(aprovar, reprovar);
    }
    @Test void webhookAusenteNaoAprova() {
        assertThatThrownBy(() -> webhook.executar(new DecisaoClienteCommand("inexistente", "APROVADO", null)))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        verifyNoInteractions(aprovar, reprovar);
    }
    @Test void decisaoDesconhecidaNaoExecutaTransicao() {
        var os = mock(OrdemServico.class);
        when(gateway.buscarPorCodigoRastreioParaAlteracao("OS-9")).thenReturn(Optional.of(os));
        when(gateway.buscarPorCodigoRastreioEClienteParaAlteracao("OS-9", 7L)).thenReturn(Optional.of(os));
        assertThatThrownBy(() -> webhook.executar(new DecisaoClienteCommand("OS-9", "TALVEZ", null))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> cliente.executar(new DecisaoClienteAutenticadoCommand("OS-9", 7L, "TALVEZ", null))).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(aprovar, reprovar);
    }
}
