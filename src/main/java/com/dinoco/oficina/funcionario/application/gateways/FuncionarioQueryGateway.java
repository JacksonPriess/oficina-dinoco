package com.dinoco.oficina.funcionario.application.gateways;

import com.dinoco.oficina.funcionario.application.usecases.queries.buscarporid.BuscarFuncionarioPorIdOutput;
import java.util.Optional;

public interface FuncionarioQueryGateway {

    boolean existePorCpf(String cpf);

    Optional<BuscarFuncionarioPorIdOutput> buscarDetalhesPorId(Long id);

}
