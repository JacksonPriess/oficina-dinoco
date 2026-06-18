package com.dinoco.oficina.funcionario.infrastructure.gateways;


import com.dinoco.oficina.funcionario.application.gateways.FuncionarioCommandGateway;
import com.dinoco.oficina.funcionario.domain.Funcionario;
import com.dinoco.oficina.funcionario.infrastructure.persistence.FuncionarioEntity;
import com.dinoco.oficina.funcionario.infrastructure.persistence.FuncionarioJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Component
public class FuncionarioCommandGatewayImpl implements FuncionarioCommandGateway {

    private final FuncionarioJpaRepository jpaRepository;

    public FuncionarioCommandGatewayImpl(FuncionarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public Funcionario salvar(Funcionario funcionarioDominio) {
        FuncionarioEntity entity = mapearParaEntity(funcionarioDominio);
        FuncionarioEntity salvo = jpaRepository.save(entity);
        return mapearParaDominio(salvo);

    }

    @Override
    public Optional<Funcionario> buscarParaAlteracao(Long id) {
        return jpaRepository.findById(id).map(this::mapearParaDominio);
    }

    private Funcionario mapearParaDominio(FuncionarioEntity entity) {
        return new Funcionario(
                entity.getId(),
                entity.getNome(),
                entity.getCpf(),
                entity.getCargo(),
                entity.isAtivo(),
                entity.getUsuarioId());
    }

    private FuncionarioEntity mapearParaEntity(Funcionario dominio) {
        FuncionarioEntity entity = new FuncionarioEntity();
        entity.setId(dominio.getId());
        entity.setNome(dominio.getNome());
        entity.setCpf(dominio.getCpf());
        entity.setCargo(dominio.getCargo());
        entity.setAtivo(dominio.isAtivo());
        entity.setUsuarioId(dominio.getUsuarioId());
        return entity;
    }

}