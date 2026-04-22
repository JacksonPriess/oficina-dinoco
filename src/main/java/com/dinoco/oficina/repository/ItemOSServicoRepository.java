package com.dinoco.oficina.repository;

import com.dinoco.oficina.entity.ItemOSServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemOSServicoRepository extends JpaRepository<ItemOSServico, Long> {}
