package com.dinoco.oficina.repository;

import com.dinoco.oficina.entity.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    Optional<OrdemServico> findByCodigoRastreio(String codigoRastreio);

    @Query("""
        SELECT DISTINCT os FROM OrdemServico os
        LEFT JOIN FETCH os.cliente
        LEFT JOIN FETCH os.veiculo
        LEFT JOIN FETCH os.itensServico itensS
        LEFT JOIN FETCH itensS.servico
        LEFT JOIN FETCH os.itensProduto itensP
        LEFT JOIN FETCH itensP.produto
        WHERE os.codigoRastreio = :codigoRastreio
    """)
    Optional<OrdemServico> buscarPorCodigoRastreioComDetalhes(@Param("codigoRastreio") String codigoRastreio);
}
