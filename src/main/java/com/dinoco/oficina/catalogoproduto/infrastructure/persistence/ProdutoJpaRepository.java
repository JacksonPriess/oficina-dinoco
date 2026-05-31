package com.dinoco.oficina.catalogoproduto.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProdutoJpaRepository extends JpaRepository<ProdutoEntity, Long> {
    boolean existsByNome(String nome);

    @Query("SELECT p FROM ProdutoEntity p WHERE p.ativo = true AND (" +
            "LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
            "LOWER(p.marca) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
            "LOWER(p.codigoFabricante) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
            "LOWER(p.aplicacao) LIKE LOWER(CONCAT('%', :termo, '%')))")
    List<ProdutoEntity> buscarPorTermo(@Param("termo") String termo);
}