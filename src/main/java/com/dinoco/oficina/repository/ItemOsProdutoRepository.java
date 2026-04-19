package com.dinoco.oficina.repository;

import com.dinoco.oficina.entity.ItemOsProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemOsProdutoRepository extends JpaRepository<ItemOsProduto, Long> {

}