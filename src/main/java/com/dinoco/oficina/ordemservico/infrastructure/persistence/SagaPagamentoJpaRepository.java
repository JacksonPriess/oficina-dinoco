package com.dinoco.oficina.ordemservico.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SagaPagamentoJpaRepository extends JpaRepository<SagaPagamentoEntity, UUID> {

    Optional<SagaPagamentoEntity> findByOsId(Long osId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SagaPagamentoEntity s where s.osId = :osId")
    Optional<SagaPagamentoEntity> bloquearPorOsId(Long osId);
}
