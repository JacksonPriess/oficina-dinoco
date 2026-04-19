package com.dinoco.oficina.repository;

import com.dinoco.oficina.entity.ItemOsServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemOsServicoRepository extends JpaRepository<ItemOsServico, Long> {}
