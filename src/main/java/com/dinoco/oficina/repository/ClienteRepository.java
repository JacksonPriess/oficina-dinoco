package com.dinoco.oficina.repository;

import com.dinoco.oficina.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByDocumento(String documento);
}
