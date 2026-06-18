package com.dinoco.oficina.funcionario.infrastructure.gateways;

import com.dinoco.oficina.funcionario.application.gateways.FuncionarioQueryGateway;
import com.dinoco.oficina.funcionario.application.usecases.queries.buscarporid.BuscarFuncionarioPorIdOutput;
import com.dinoco.oficina.funcionario.infrastructure.persistence.FuncionarioEntity;
import com.dinoco.oficina.funcionario.infrastructure.persistence.FuncionarioJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class FuncionarioQueryGatewayImpl implements FuncionarioQueryGateway {

    private final FuncionarioJpaRepository jpaRepository;

    public FuncionarioQueryGatewayImpl(FuncionarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existePorCpf(String cpf) {
        return jpaRepository.existsByCpf(cpf);
    }

    @Override
    public Optional<BuscarFuncionarioPorIdOutput> buscarDetalhesPorId(Long id) {
        return jpaRepository.findById(id).map(this::mapearParaOutputVisual);
    }

    private BuscarFuncionarioPorIdOutput mapearParaOutputVisual(FuncionarioEntity entity){

        return new BuscarFuncionarioPorIdOutput(
                entity.getId(),
                entity.getNome(),
                entity.getCpf(),
                entity.getCargo(),
                entity.isAtivo(),
                entity.getUsuarioId()
        );
    }
}
