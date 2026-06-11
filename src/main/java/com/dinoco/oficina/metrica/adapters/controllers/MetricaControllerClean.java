package com.dinoco.oficina.metrica.adapters.controllers;

import com.dinoco.oficina.metrica.application.usecase.queries.buscarmediaexecucao.BuscarMediaExecucaoServicosOutput;
import com.dinoco.oficina.metrica.application.usecase.queries.buscarmediaexecucao.BuscarMediaExecucaoServicosQuery;
import com.dinoco.oficina.metrica.application.usecase.queries.buscarmediaexecucao.BuscarMediaExecucaoServicosUseCase;

/**
 * Orquestra commands e queries
 */
public class MetricaControllerClean {

    private final BuscarMediaExecucaoServicosUseCase buscarMediaExecucaoServicosUseCase;

    public MetricaControllerClean(BuscarMediaExecucaoServicosUseCase buscarMediaExecucaoServicosUseCase) {
        this.buscarMediaExecucaoServicosUseCase = buscarMediaExecucaoServicosUseCase;
    }

    public BuscarMediaExecucaoServicosOutput buscarMediaExecucaoServicos(BuscarMediaExecucaoServicosQuery query){
        return buscarMediaExecucaoServicosUseCase.executar(query);
    }
}
