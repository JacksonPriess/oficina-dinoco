package com.dinoco.oficina.ordemservico.application.gateways;

import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarporid.BuscarOSPorIdOuput;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarpornumero.BuscarOSPorCodigoRastreioOuput;
import com.dinoco.oficina.ordemservico.application.usecases.queries.listarfilatrabalho.ListarFilaTrabalhoDetalhesOutput;

import java.util.List;
import java.util.Optional;

public interface OrdemServicoQueryGateway {

    Optional<BuscarOSPorIdOuput> buscarPorId(Long id);
    Optional<BuscarOSPorCodigoRastreioOuput> buscarPorCodigoRastreio(String codigoRastreio);
    List<ListarFilaTrabalhoDetalhesOutput> listarFilaDeTrabalho();
}
