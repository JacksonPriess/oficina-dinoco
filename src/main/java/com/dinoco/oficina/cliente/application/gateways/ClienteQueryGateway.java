package com.dinoco.oficina.cliente.application.gateways;

import com.dinoco.oficina.cliente.application.usecases.queries.buscarporid.BuscarClientePorIdOutput;
import java.util.Optional;

public interface ClienteQueryGateway {

    boolean existePorDocumento(String documento);

    Optional<BuscarClientePorIdOutput> buscarDetalhesPorId(Long id);
}