package com.dinoco.oficina.cliente.infrastructure.gateways;

import com.dinoco.oficina.cliente.application.gateways.ClienteCommandGateway;
import com.dinoco.oficina.cliente.domain.Cliente;
import com.dinoco.oficina.cliente.domain.Endereco;
import com.dinoco.oficina.cliente.infrastructure.persistence.ClienteEntity;
import com.dinoco.oficina.cliente.infrastructure.persistence.ClienteJpaRepository;
import com.dinoco.oficina.cliente.infrastructure.persistence.EnderecoEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ClienteCommandGatewayImpl implements ClienteCommandGateway {

    private final ClienteJpaRepository jpaRepository;

    public ClienteCommandGatewayImpl(ClienteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public Cliente salvar(Cliente clienteDominio) {
        ClienteEntity entity = mapearParaEntity(clienteDominio);
        ClienteEntity salvo = jpaRepository.save(entity);
        return mapearParaDominio(salvo);
    }

    @Override
    public Optional<Cliente> buscarParaAlteracao(Long id) {
        return jpaRepository.findById(id).map(this::mapearParaDominio);
    }

    private ClienteEntity mapearParaEntity(Cliente dominio) {
        ClienteEntity entity = new ClienteEntity();
        entity.setId(dominio.getId());
        entity.setTipoPessoa(dominio.getTipoPessoa());
        entity.setDocumento(dominio.getDocumento());
        entity.setNome(dominio.getNome());
        entity.setInscricaoEstadual(dominio.getInscricaoEstadual());
        entity.setNomeFantasia(dominio.getNomeFantasia());
        entity.setEmail(dominio.getEmail());
        entity.setTelefone(dominio.getTelefone());
        entity.setAtivo(dominio.getAtivo());
        entity.setDataCriacao(dominio.getDataCriacao());

        if (dominio.getEnderecos() != null) {
            List<EnderecoEntity> enderecosEntity = dominio.getEnderecos().stream().map(endDominio -> {
                EnderecoEntity endEntity = new EnderecoEntity();
                endEntity.setCep(endDominio.getCep());
                endEntity.setLogradouro(endDominio.getLogradouro());
                endEntity.setNumero(endDominio.getNumero());
                endEntity.setComplemento(endDominio.getComplemento());
                endEntity.setBairro(endDominio.getBairro());
                endEntity.setCidade(endDominio.getCidade());
                endEntity.setUf(endDominio.getUf());
                endEntity.setCliente(entity);
                return endEntity;
            }).collect(Collectors.toList());

            entity.setEnderecos(enderecosEntity);
        }
        return entity;
    }

    private Cliente mapearParaDominio(ClienteEntity entity) {
        List<Endereco> enderecosDominio = entity.getEnderecos().stream().map(endEntity ->
                new Endereco(
                        endEntity.getCep(), endEntity.getLogradouro(), endEntity.getNumero(),
                        endEntity.getComplemento(), endEntity.getBairro(), endEntity.getCidade(),
                        endEntity.getUf()
                )
        ).collect(Collectors.toList());

        return new Cliente(
                entity.getId(), entity.getTipoPessoa(), entity.getDocumento(), entity.getNome(),
                entity.getInscricaoEstadual(), entity.getNomeFantasia(), entity.getEmail(),
                entity.getTelefone(), entity.getAtivo(), entity.getDataCriacao(), enderecosDominio
        );
    }

}
