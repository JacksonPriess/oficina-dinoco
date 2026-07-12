package com.dinoco.oficina.ordemservico.application.usecases.queries.listarfilatrabalho;


import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoQueryGateway;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class ListarFilaTrabalhoHandler implements ListarFilaTrabalhoUseCase {

    private final OrdemServicoQueryGateway ordemServicoQueryGateway;

    public ListarFilaTrabalhoHandler(OrdemServicoQueryGateway ordemServicoQueryGateway) {
        this.ordemServicoQueryGateway = ordemServicoQueryGateway;
    }

    @Override
    public ListarFilaTrabalhoOutput executar(ListarFilaTrabalhoQuery query) {
        log.info("Buscando fila de trabalhos");
        List<ListarFilaTrabalhoDetalhesOutput> filaDeTrabalhos = ordemServicoQueryGateway.listarFilaDeTrabalho();
        return new ListarFilaTrabalhoOutput(LocalDateTime.now(), filaDeTrabalhos);
    }
}