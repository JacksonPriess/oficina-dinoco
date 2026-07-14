package com.dinoco.oficina.veiculo.infrastructure.gateways;

import com.dinoco.oficina.veiculo.application.gateways.VeiculoQueryGateway;
import com.dinoco.oficina.veiculo.application.usecases.queries.buscarporid.BuscarVeiculoPorIdOutput;
import com.dinoco.oficina.veiculo.infrastructure.persistence.VeiculoEntity;
import com.dinoco.oficina.veiculo.infrastructure.persistence.VeiculoJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class VeiculoQueryGatewayImpl implements VeiculoQueryGateway {

    private final VeiculoJpaRepository jpaRepository;

    public VeiculoQueryGatewayImpl(VeiculoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existePorPlaca(String placa) {
        return jpaRepository.existsByPlaca(placa);
    }

    @Override
    public Optional<BuscarVeiculoPorIdOutput> buscarDetalhesPorId(Long id) {
        return jpaRepository.findById(id).map(this::mapearParaOutputVisual);
    }

    private BuscarVeiculoPorIdOutput mapearParaOutputVisual(VeiculoEntity entity) {
        // Devolve o Record DTO que a UseCase/Query exige
        return new BuscarVeiculoPorIdOutput(
                entity.getId(),
                entity.getPlaca(),
                entity.getMarca(),
                entity.getModelo(),
                entity.getAnoFabricacao(),
                entity.getAnoModelo(),
                entity.getCor(),
                entity.getChassi(),
                entity.getMotor(),
                entity.getAtivo(),
                entity.getDataCriacao()
        );
    }
}
