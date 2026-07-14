package com.dinoco.oficina.ordemservico.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrdemServicoJpaRepository extends JpaRepository<OrdemServicoEntity, Long> {

    Optional<OrdemServicoEntity> findByCodigoRastreio(String codigoRastreio);

}