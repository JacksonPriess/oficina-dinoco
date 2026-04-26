package com.dinoco.oficina.service;

import com.dinoco.oficina.dto.ItemOSProdutoAdicionarDto;
import com.dinoco.oficina.dto.ItemOSProdutoAlterarDto;
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
    public void adicionarItemProduto(Long osId, ItemOSProdutoAdicionarDto dto) {
        OrdemServico os = ordemServicoService.buscarOuFalhar(osId);
        validarSePodeModificarItens(os);
        Produto produto = produtoService.buscarEntidadePorId(dto.produtoId());
        ItemOSProduto item = new ItemOSProduto();
        item.setOrdemServico(os);
        item.setProduto(produto);
        item.setQuantidade(dto.quantidade());
        item.setValorUnitarioVenda(produto.getPrecoVenda());
        item.setValorTotal(dto.quantidade().multiply(produto.getPrecoVenda()));
        repository.save(item);
        ordemServicoService.recalcularTotais(osId);
    }

    @Transactional
    public void alterarItemProduto(Long itemId, ItemOSProdutoAlterarDto dto) {
        ItemOSProduto item = buscarItemOuFalhar(itemId);
        OrdemServico os = item.getOrdemServico();
        validarSePodeModificarItens(os);
        validarValorUnitarioVenda(dto, item);
        item.setQuantidade(dto.quantidade());
        item.setValorUnitarioVenda(dto.valorUnitarioVenda());
        item.setValorTotal(dto.quantidade().multiply(dto.valorUnitarioVenda()));
        repository.save(item);
        ordemServicoService.recalcularTotais(os.getId());
    }

    private static void validarValorUnitarioVenda(ItemOSProdutoAlterarDto dto, ItemOSProduto item) {
        Produto produtoOriginal = item.getProduto();
        if (produtoOriginal.isValorVendaInvalido(dto.valorUnitarioVenda())) {
            throw new IllegalStateException("O valor de venda não pode ser menor que o preço de custo.");
        }
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