package com.dinoco.oficina.repository;

import com.dinoco.oficina.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository <Produto,Long> {

    /**
     * Quando o usuário procurar por uma produto, ele vai informar um termpo,
     * e a app vai tentar encontrar o produto utilizando o termo em várias colunas.
     */
    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND (" +
            "LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
            "LOWER(p.marca) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
            "LOWER(p.codigoFabricante) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
            "LOWER(p.aplicacao) LIKE LOWER(CONCAT('%', :termo, '%')))")
    List<Produto> buscaAvancada(@Param("termo") String termo);


}
