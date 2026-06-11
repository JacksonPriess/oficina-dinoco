package com.dinoco.oficina.catalogoproduto.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoJpaRepository extends JpaRepository<ProdutoEntity, Long> {
    boolean existsByNome(String nome);
}