package com.dinoco.oficina.ordemservico.infrastructure.gateways;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.domain.models.ItemOSProduto;
import com.dinoco.oficina.ordemservico.domain.models.ItemOSServico;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import com.dinoco.oficina.ordemservico.infrastructure.persistence.ItemOSProdutoEntity;
import com.dinoco.oficina.ordemservico.infrastructure.persistence.ItemOSServicoEntity;
import com.dinoco.oficina.ordemservico.infrastructure.persistence.OrdemServicoEntity;
import com.dinoco.oficina.ordemservico.infrastructure.persistence.OrdemServicoJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class OrdemServicoCommandGatewayImpl implements OrdemServicoCommandGateway {

    private final OrdemServicoJpaRepository jpaRepository;

    public OrdemServicoCommandGatewayImpl(OrdemServicoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<OrdemServico> buscarParaAlteracao(Long id) {
        return jpaRepository.findById(id).map(this::mapearParaDominio);
    }

    @Override
    public Optional<OrdemServico> buscarPorCodigoRastreioParaAlteracao(String codigoRastreio) {
        return jpaRepository.findByCodigoRastreio(codigoRastreio).map(this::mapearParaDominio);
    }

    @Override
    public Optional<OrdemServico> buscarPorCodigoRastreioEClienteParaAlteracao(String codigoRastreio, Long clienteId) {
        return jpaRepository.findByCodigoRastreioAndClienteId(codigoRastreio, clienteId).map(this::mapearParaDominio);
    }

    @Override
    @Transactional
    public OrdemServico salvar(OrdemServico ordemServico) {
        OrdemServicoEntity entity = mapearParaEntity(ordemServico);
        OrdemServicoEntity salvo = jpaRepository.save(entity);
        return mapearParaDominio(salvo);
    }

    private OrdemServicoEntity mapearParaEntity(OrdemServico dominio) {
        OrdemServicoEntity entity = new OrdemServicoEntity();
        entity.setId(dominio.getId());
        entity.setCodigoRastreio(dominio.getCodigoRastreio());
        entity.setClienteId(dominio.getClienteId());
        entity.setVeiculoId(dominio.getVeiculoId());
        entity.setStatus(dominio.getStatus());
        entity.setReclamacaoCliente(dominio.getReclamacaoCliente());
        entity.setLaudoTecnico(dominio.getLaudoTecnico());
        entity.setQuilometragemEntrada(dominio.getQuilometragemEntrada());
        entity.setValorDesconto(dominio.getValorDesconto());
        entity.setValorTotalServicos(dominio.getValorTotalServicos());
        entity.setValorTotalProdutos(dominio.getValorTotalProdutos());
        entity.setValorTotalOS(dominio.getValorTotalOS());
        entity.setDataSaida(dominio.getDataSaida());
        entity.setDataReprovacao(dominio.getDataReprovacao());

        if (dominio.getItensProduto() != null) {
            for (ItemOSProduto itemDominio : dominio.getItensProduto()) {
                ItemOSProdutoEntity itemEntity = new ItemOSProdutoEntity();
                itemEntity.setId(itemDominio.getId());
                itemEntity.setProdutoId(itemDominio.getProdutoId());
                itemEntity.setQuantidade(itemDominio.getQuantidade());
                itemEntity.setValorUnitarioVenda(itemDominio.getValorUnitarioVenda());
                itemEntity.setValorTotal(itemDominio.getValorTotal());
                itemEntity.setOrdemServico(entity);
                entity.getItensProduto().add(itemEntity);
            }
        }

        if (dominio.getItensServico() != null) {
            for (ItemOSServico itemDominio : dominio.getItensServico()) {
                ItemOSServicoEntity itemEntity = new ItemOSServicoEntity();
                itemEntity.setId(itemDominio.getId());
                itemEntity.setServicoId(itemDominio.getServicoId());
                itemEntity.setMecanicoId(itemDominio.getMecanicoId());
                itemEntity.setValorCobrado(itemDominio.getValorCobrado());
                itemEntity.setStatusItem(itemDominio.getStatusItem());
                itemEntity.setDataInicio(itemDominio.getDataInicio());
                itemEntity.setDataFim(itemDominio.getDataFim());
                itemEntity.setOrdemServico(entity);
                entity.getItensServico().add(itemEntity);
            }
        }

        return entity;
    }

    private OrdemServico mapearParaDominio(OrdemServicoEntity entity) {
        OrdemServico ordemServicoDominio = new OrdemServico(
                entity.getId(),
                entity.getCodigoRastreio(),
                entity.getClienteId(),
                entity.getVeiculoId(),
                entity.getStatus(),
                entity.getReclamacaoCliente(),
                entity.getLaudoTecnico(),
                entity.getQuilometragemEntrada(),
                entity.getValorDesconto(),
                entity.getValorTotalServicos(),
                entity.getValorTotalProdutos(),
                entity.getValorTotalOS(),
                entity.getDataEntrada(),
                entity.getDataSaida(),
                entity.getDataReprovacao()
        );

        if (entity.getItensProduto() != null) {
            entity.getItensProduto().forEach(itemEntity -> {
                ItemOSProduto itemDominio = new ItemOSProduto(
                        itemEntity.getId(),
                        itemEntity.getProdutoId(),
                        itemEntity.getQuantidade(),
                        itemEntity.getValorUnitarioVenda()
                );
                ordemServicoDominio.getItensProduto().add(itemDominio);
            });
        }

        if (entity.getItensServico() != null) {
            entity.getItensServico().forEach(itemEntity -> {
                ItemOSServico itemDominio = new ItemOSServico(
                        itemEntity.getId(),
                        itemEntity.getServicoId(),
                        itemEntity.getMecanicoId(),
                        itemEntity.getValorCobrado(),
                        itemEntity.getStatusItem(),
                        itemEntity.getDataInicio(),
                        itemEntity.getDataFim()
                );
                ordemServicoDominio.getItensServico().add(itemDominio);
            });
        }

        ordemServicoDominio.recalcularTotais();

        return ordemServicoDominio;
    }
}