package com.dinoco.oficina.catalogoproduto.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProdutoEstoqueJpaRepository extends JpaRepository<ProdutoEstoqueEntity, Long> {

    @Query("SELECT v FROM ProdutoEstoqueEntity v " +
            "WHERE v.ativo = true AND (" +
            "LOWER(v.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
            "LOWER(v.marca) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
            "LOWER(v.codigoFabricante) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
            "LOWER(v.aplicacao) LIKE LOWER(CONCAT('%', :termo, '%')))")
    List<ProdutoEstoqueEntity> buscarPorTermo(@Param("termo") String termo);
}