package com.dinoco.oficina.catalogoproduto.infrastructure.persistence;

import com.dinoco.oficina.catalogoproduto.application.gateways.ProdutoQueryGateway;
import com.dinoco.oficina.catalogoproduto.application.usecases.queries.buscarportermo.BuscarProdutoPorTermoOutput;
import com.dinoco.oficina.catalogoproduto.application.usecases.queries.buscarporid.BuscarProdutoPorIdOutput;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProdutoQueryGatewayImpl implements ProdutoQueryGateway {
    private final ProdutoJpaRepository jpaRepository;

    public ProdutoQueryGatewayImpl(ProdutoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existePorNome(String nome) {
        return jpaRepository.existsByNome(nome);
    }

    @Override
    public Optional<BuscarProdutoPorIdOutput> buscarDetalhesPorId(Long id) {
        return jpaRepository.findById(id).map(this::mapearParaOutputVisual);
    }

    @Override
    public List<BuscarProdutoPorTermoOutput> buscarDetalhesPorTermo(String termo) {
        return jpaRepository.buscarPorTermo(termo).stream().map(this::mapearParaBuscaAvancadaOutput).toList();
    }

    private BuscarProdutoPorTermoOutput mapearParaBuscaAvancadaOutput(ProdutoEntity entity) {
        // Devolve o Record DTO que a UseCase/Query exige
        return new BuscarProdutoPorTermoOutput(
                entity.getId(),
                entity.getVersion(),
                entity.getNome(),
                entity.getTipo(),
                entity.getMarca(),
                entity.getCodigoFabricante(),
                entity.getAplicacao(),
                entity.getQuantidadeAtual(),
                entity.getQuantidadeReservada(),
                entity.getPrecoCusto(),
                entity.getPrecoVenda(),
                entity.getAtivo()
        );

    }

    private BuscarProdutoPorIdOutput mapearParaOutputVisual(ProdutoEntity entity) {
        // Devolve o Record DTO que a UseCase/Query exige
        return new BuscarProdutoPorIdOutput(
                entity.getId(),
                entity.getVersion(),
                entity.getNome(),
                entity.getTipo(),
                entity.getMarca(),
                entity.getCodigoFabricante(),
                entity.getAplicacao(),
                entity.getQuantidadeAtual(),
                entity.getQuantidadeReservada(),
                entity.getPrecoCusto(),
                entity.getPrecoVenda(),
                entity.getAtivo()
        );
    }
}