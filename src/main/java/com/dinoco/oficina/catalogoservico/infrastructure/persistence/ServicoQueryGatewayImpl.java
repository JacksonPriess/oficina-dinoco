package com.dinoco.oficina.catalogoservico.infrastructure.persistence;

import com.dinoco.oficina.catalogoservico.application.gateways.ServicoQueryGateway;
import com.dinoco.oficina.catalogoservico.application.usecases.queries.buscarporid.BuscarServicoPorIdOutput;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class ServicoQueryGatewayImpl implements ServicoQueryGateway {
    private final ServicoJpaRepository jpaRepository;

    public ServicoQueryGatewayImpl(ServicoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existePorDescricao(String descricao) {
        return jpaRepository.existsByDescricaoIgnoreCase(descricao);
    }

    @Override
    public Optional<BuscarServicoPorIdOutput> buscarDetalhesPorId(Long id) {
        return jpaRepository.findById(id).map(this::mapearParaOutputVisual);
    }

    private BuscarServicoPorIdOutput mapearParaOutputVisual(ServicoEntity entity) {
        // Devolve o Record DTO que a UseCase/Query exige
        return new BuscarServicoPorIdOutput(
                entity.getId(),
                entity.getDescricao(),
                entity.getPrecoPadrao(),
                entity.getTempoEstimadoMinutos(),
                entity.getAtivo(),
                entity.getDataCriacao()
        );
    }
}