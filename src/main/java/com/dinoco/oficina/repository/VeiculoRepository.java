package com.dinoco.oficina.repository;

import com.dinoco.oficina.entity.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    boolean existsByPlaca(String placa);
    List<Veiculo> findByClienteId(Long clienteId);
}
