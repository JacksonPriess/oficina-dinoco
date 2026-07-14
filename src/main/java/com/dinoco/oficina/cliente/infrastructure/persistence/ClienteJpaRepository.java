package com.dinoco.oficina.cliente.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, Long> {
    boolean existsByDocumento(String documento);
}
