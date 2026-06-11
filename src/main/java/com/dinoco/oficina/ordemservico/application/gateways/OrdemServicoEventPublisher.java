package com.dinoco.oficina.ordemservico.application.gateways;

import com.dinoco.oficina.shared.events.ExecucaoIniciadaEvent;
import com.dinoco.oficina.shared.events.OrcamentoAprovadoEvent;

public interface OrdemServicoEventPublisher {

    void publicarOrcamentoAprovado(OrcamentoAprovadoEvent event);
    void publicarExecucaoIniciada(ExecucaoIniciadaEvent event);

}
