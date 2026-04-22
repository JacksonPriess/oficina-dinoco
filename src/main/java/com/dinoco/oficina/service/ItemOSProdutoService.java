package com.dinoco.oficina.service;

import com.dinoco.oficina.dto.ItemProdutoAdicionarDto;
import com.dinoco.oficina.dto.ItemProdutoAlterarDto;
import com.dinoco.oficina.entity.ItemOSProduto;
import com.dinoco.oficina.entity.OrdemServico;
import com.dinoco.oficina.entity.Produto;
import com.dinoco.oficina.enums.StatusOS;
import com.dinoco.oficina.repository.ItemOSProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemOSProdutoService {

    private final ItemOSProdutoRepository repository;
    private final OrdemServicoService ordemServicoService;
    private final ProdutoService produtoService;

    @Transactional
    public void adicionarItemProduto(Long osId, ItemProdutoAdicionarDto dto) {
        OrdemServico os = ordemServicoService.buscarOuFalhar(osId);
        if (os.getStatus() != StatusOS.EM_DIAGNOSTICO) {
            throw new IllegalStateException("Inicie o diagnóstico da OS antes de adicionar itens de produto.");
        }
        Produto produto = produtoService.buscarEntidadePorId(dto.produtoId());
        ItemOSProduto item = new ItemOSProduto();
        item.setOrdemServico(os);
        item.setProduto(produto);
        item.setQuantidade(dto.quantidade());
        item.setValorUnitarioVenda(dto.valorUnitarioVenda());
        item.setValorTotal(dto.quantidade().multiply(dto.valorUnitarioVenda()));
        repository.save(item);
        ordemServicoService.recalcularTotais(osId);
    }

    @Transactional
    public void alterarItemProduto(Long itemId, ItemProdutoAlterarDto dto) {
        ItemOSProduto item = buscarItemOuFalhar(itemId);
        OrdemServico os = item.getOrdemServico();
        validarSePodeModificarItens(os);
        item.setQuantidade(dto.quantidade());
        item.setValorUnitarioVenda(dto.valorUnitarioVenda());
        item.setValorTotal(dto.quantidade().multiply(dto.valorUnitarioVenda()));
        repository.save(item);
        ordemServicoService.recalcularTotais(os.getId());
    }

    @Transactional
    public void removerItemProduto(Long itemId) {
        ItemOSProduto item = buscarItemOuFalhar(itemId);
        OrdemServico os = item.getOrdemServico();
        validarSePodeModificarItens(os);
        os.getItensProduto().remove(item);
        repository.delete(item);
        ordemServicoService.recalcularTotais(os.getId());
    }

    private ItemOSProduto buscarItemOuFalhar(Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item de produto não encontrado."));
    }

    private void validarSePodeModificarItens(OrdemServico os) {
        if (os.getStatus() != StatusOS.EM_DIAGNOSTICO && os.getStatus() != StatusOS.AGUARDANDO_ORCAMENTO) {
            throw new IllegalStateException("Não é possível modificar itens de uma OS que já saiu da fase de orçamento. Status atual: " + os.getStatus());
        }
    }
}