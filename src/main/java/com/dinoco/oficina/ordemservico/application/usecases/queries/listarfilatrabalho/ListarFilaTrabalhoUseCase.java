package com.dinoco.oficina.ordemservico.application.usecases.queries.listarfilatrabalho;

public interface ListarFilaTrabalhoUseCase {
    ListarFilaTrabalhoOutput executar(ListarFilaTrabalhoQuery query);
}