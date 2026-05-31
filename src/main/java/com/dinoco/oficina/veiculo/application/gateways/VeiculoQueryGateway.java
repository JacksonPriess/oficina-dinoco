package com.dinoco.oficina.veiculo.application.gateways;

import com.dinoco.oficina.veiculo.application.usecases.queries.buscarporid.BuscarVeiculoPorIdOutput;
import java.util.Optional;

public interface VeiculoQueryGateway {

    boolean existePorPlaca(String placa);
    Optional<BuscarVeiculoPorIdOutput> buscarDetalhesPorId(Long id);
}