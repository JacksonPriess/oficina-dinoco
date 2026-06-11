package com.dinoco.oficina.ordemservico.application.gateways;

import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarporid.BuscarOSPorIdOuput;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarpornumero.BuscarOSPorCodigoRastreioOuput;

import java.util.Optional;

public interface OrdemServicoQueryGateway {
    Optional<BuscarOSPorIdOuput> buscarPorId(Long id);
    Optional<BuscarOSPorCodigoRastreioOuput> buscarPorCodigoRastreio(String codigoRastreio);

}
