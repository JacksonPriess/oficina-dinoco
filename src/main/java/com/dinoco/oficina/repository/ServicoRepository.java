package com.dinoco.oficina.repository;

import com.dinoco.oficina.entity.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {
    boolean existsByDescricaoIgnoreCase(String nome);
}