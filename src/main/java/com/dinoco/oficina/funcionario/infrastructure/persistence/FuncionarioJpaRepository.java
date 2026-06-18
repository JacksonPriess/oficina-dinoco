package com.dinoco.oficina.funcionario.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionarioJpaRepository extends JpaRepository<FuncionarioEntity, Long> {
    boolean existsByCpf(String cpf);

}
