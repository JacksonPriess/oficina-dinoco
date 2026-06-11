package com.dinoco.oficina.estoque.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoEstoqueJpaRepository extends JpaRepository<MovimentacaoEstoqueEntity, Long> {
}