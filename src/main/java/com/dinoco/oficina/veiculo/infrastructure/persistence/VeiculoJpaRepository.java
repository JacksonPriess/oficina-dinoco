package com.dinoco.oficina.veiculo.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VeiculoJpaRepository extends JpaRepository<VeiculoEntity, Long> {
    boolean existsByPlaca(String placa);
}
