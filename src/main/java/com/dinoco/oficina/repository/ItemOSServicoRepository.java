package com.dinoco.oficina.repository;

import com.dinoco.oficina.entity.ItemOSServico;
import com.dinoco.oficina.repository.projection.ServicoMetricaProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemOSServicoRepository extends JpaRepository<ItemOSServico, Long> {
    boolean existsByOrdemServicoIdAndServicoId(Long osId, Long servicoId);

    @Query(value = """
        SELECT 
            s.id AS servicoId,
            s.descricao AS descricao,
            COUNT(i.id) AS quantidade,
            AVG(EXTRACT(EPOCH FROM (i.data_fim - i.data_inicio)) / 60) AS mediaMinutos,
            s.tempo_estimado_minutos AS tempoPadraoMinutos
        FROM item_os_servico i
        JOIN servico s ON s.id = i.servico_id
        WHERE i.status_item = 'CONCLUIDO'
          AND (CAST(:inicio AS timestamp) IS NULL OR i.data_fim >= CAST(:inicio AS timestamp))
          AND (CAST(:fim AS timestamp) IS NULL OR i.data_fim <= CAST(:fim AS timestamp))
        GROUP BY s.id, s.descricao, s.tempo_estimado_minutos
        """, nativeQuery = true)
    List<ServicoMetricaProjection> consultarMetricas(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}
