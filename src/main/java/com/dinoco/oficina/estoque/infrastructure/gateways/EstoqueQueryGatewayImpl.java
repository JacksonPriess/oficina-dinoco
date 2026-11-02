package com.dinoco.oficina.estoque.infrastructure.gateways;

import com.dinoco.oficina.estoque.application.gateways.EstoqueQueryGateway;
import com.dinoco.oficina.estoque.application.usecases.queries.buscarporid.BuscarEstoquePorIdOuput;
import com.dinoco.oficina.estoque.infrastructure.persistence.SaldoEstoqueEntity;
import com.dinoco.oficina.estoque.infrastructure.persistence.SaldoEstoqueJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class EstoqueQueryGatewayImpl implements EstoqueQueryGateway {

    private final SaldoEstoqueJpaRepository jpaRepository;

    public EstoqueQueryGatewayImpl(SaldoEstoqueJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<BuscarEstoquePorIdOuput> buscarPorProdutoId(Long produtoId) {
        return jpaRepository.findByProdutoId(produtoId).map(this::mapearParaOutput);
    }

    private BuscarEstoquePorIdOuput mapearParaOutput(SaldoEstoqueEntity entity) {
        return new BuscarEstoquePorIdOuput(
                entity.getId(),
                entity.getProdutoId(),
                entity.getQuantidadeReal(),
                entity.getQuantidadeReservada()
        );
    }
}