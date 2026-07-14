package com.dinoco.oficina.veiculo.infrastructure.gateways;

import com.dinoco.oficina.veiculo.application.gateways.VeiculoCommandGateway;
import com.dinoco.oficina.veiculo.domain.Veiculo;
import com.dinoco.oficina.veiculo.infrastructure.persistence.VeiculoEntity;
import com.dinoco.oficina.veiculo.infrastructure.persistence.VeiculoJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class VeiculoCommandGatewayImpl implements VeiculoCommandGateway {

    private final VeiculoJpaRepository jpaRepository;

    public VeiculoCommandGatewayImpl(VeiculoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public Veiculo salvar(Veiculo veiculoDominio) {
        VeiculoEntity entity = mapearParaEntity(veiculoDominio);
        VeiculoEntity salvo = jpaRepository.save(entity);
        return mapearParaDominio(salvo);
    }

    @Override
    public Optional<Veiculo> buscarParaAlteracao(Long id) {
        return jpaRepository.findById(id).map(this::mapearParaDominio);
    }

    private VeiculoEntity mapearParaEntity(Veiculo dominio) {
        VeiculoEntity entity = new VeiculoEntity();
        entity.setId(dominio.getId());
        entity.setPlaca(dominio.getPlaca());
        entity.setMarca(dominio.getMarca());
        entity.setModelo(dominio.getModelo());
        entity.setAnoFabricacao(dominio.getAnoFabricacao());
        entity.setAnoModelo(dominio.getAnoModelo());
        entity.setCor(dominio.getCor());
        entity.setChassi(dominio.getChassi());
        entity.setMotor(dominio.getMotor());
        entity.setAtivo(dominio.getAtivo());
        entity.setDataCriacao(dominio.getDataCriacao());
        return entity;
    }

    private Veiculo mapearParaDominio(VeiculoEntity entity) {
        return new Veiculo(
                entity.getId(),
                entity.getPlaca(),
                entity.getMarca(),
                entity.getModelo(),
                entity.getAnoFabricacao(),
                entity.getAnoModelo(),
                entity.getCor(),
                entity.getChassi(),
                entity.getMotor(),
                entity.getAtivo(),
                entity.getDataCriacao()
        );
    }
}