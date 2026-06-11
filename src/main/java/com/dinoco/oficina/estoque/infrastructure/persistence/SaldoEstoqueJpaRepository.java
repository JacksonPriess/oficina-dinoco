package com.dinoco.oficina.estoque.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SaldoEstoqueJpaRepository extends JpaRepository<SaldoEstoqueEntity, Long> {
    Optional<SaldoEstoqueEntity> findByProdutoId(Long produtoId);
}
