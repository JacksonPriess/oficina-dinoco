package com.dinoco.oficina.repository;

import com.dinoco.oficina.entity.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    @Query("SELECT COUNT(os) > 0 FROM OrdemServico os " +
            "WHERE os.veiculo.id = :veiculoId " +
            "AND os.status NOT IN (StatusOS.FINALIZADA, StatusOS.ENTREGUE, StatusOS.REPROVADA)")
    boolean existeOsAtivaParaVeiculo(@Param("veiculoId") Long veiculoId);

    Optional<OrdemServico> findByCodigoRastreio(String codigoRastreio);
}
