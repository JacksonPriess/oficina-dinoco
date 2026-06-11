package com.dinoco.oficina.catalogoproduto.infrastructure.gateways;

import com.dinoco.oficina.catalogoproduto.application.gateways.ProdutoQueryGateway;
import com.dinoco.oficina.catalogoproduto.application.usecases.queries.ProdutoQueryOutput;
import com.dinoco.oficina.catalogoproduto.infrastructure.persistence.ProdutoEstoqueJpaRepository;
import com.dinoco.oficina.catalogoproduto.infrastructure.persistence.ProdutoEstoqueEntity;
import com.dinoco.oficina.catalogoproduto.infrastructure.persistence.ProdutoJpaRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class ProdutoQueryGatewayImpl implements ProdutoQueryGateway {

    private final ProdutoJpaRepository jpaRepository;
    private final ProdutoEstoqueJpaRepository jpaProdutoEstoqueJpaRepository;

    public ProdutoQueryGatewayImpl(ProdutoJpaRepository jpaRepository,
                                   ProdutoEstoqueJpaRepository jpaProdutoEstoqueJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.jpaProdutoEstoqueJpaRepository = jpaProdutoEstoqueJpaRepository;
    }

    @Override
    public boolean existePorNome(String nome) {
        return jpaRepository.existsByNome(nome);
    }

    @Override
    public Optional<ProdutoQueryOutput> buscarPorId(Long id) {
        return jpaProdutoEstoqueJpaRepository.findById(id)
                .map(this::mapearParaOutput);
    }

    @Override
    public List<ProdutoQueryOutput> buscarPorTermo(String termo) {
        List<ProdutoEstoqueEntity> entidades;

        if (termo == null || termo.trim().isEmpty()) {
            entidades = jpaProdutoEstoqueJpaRepository.findAll();
        } else {
            entidades = jpaProdutoEstoqueJpaRepository.buscarPorTermo(termo);
        }

        return entidades.stream()
                .map(this::mapearParaOutput)
                .toList();
    }

    private ProdutoQueryOutput mapearParaOutput(ProdutoEstoqueEntity entity) {
        return new ProdutoQueryOutput(
                entity.getId(),
                entity.getVersion(),
                entity.getNome(),
                entity.getTipo(),
                entity.getMarca(),
                entity.getCodigoFabricante(),
                entity.getAplicacao(),
                entity.getPrecoCusto(),
                entity.getPrecoVenda(),
                entity.getAtivo(),
                entity.getQuantidadeReal(),
                entity.getQuantidadeReservada()
        );
    }
}