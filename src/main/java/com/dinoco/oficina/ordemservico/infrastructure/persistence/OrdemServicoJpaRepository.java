package com.dinoco.oficina.ordemservico.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface OrdemServicoJpaRepository extends JpaRepository<OrdemServicoEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from OrdemServicoEntity o where o.id = :id")
    Optional<OrdemServicoEntity> bloquearPorId(Long id);

    Optional<OrdemServicoEntity> findByCodigoRastreio(String codigoRastreio);

    Optional<OrdemServicoEntity> findByCodigoRastreioAndClienteId(
            String codigoRastreio,
            Long clienteId
    );
}