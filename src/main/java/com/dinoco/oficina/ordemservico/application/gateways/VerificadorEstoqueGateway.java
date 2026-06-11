package com.dinoco.oficina.ordemservico.application.gateways;

import com.dinoco.oficina.ordemservico.domain.models.ItemOSProduto;
import com.dinoco.oficina.ordemservico.infrastructure.web.dto.PecaPendenteDto;

import java.util.List;

public interface VerificadorEstoqueGateway {
    /**
     * Verifica instantaneamente se há saldo físico disponível (Real - Reservado)
     * para cobrir todas as peças solicitadas na Ordem de Serviço.
     */
    boolean todasAsPecasEstaoDisponiveis(List<ItemOSProduto> itensProduto);

    /**
     * Retorna uma lista com os produtos que não possuem saldo suficiente no estoque.
     * Se a lista retornada for vazia, significa que TODAS as peças estão disponíveis.
     */
    List<PecaPendenteDto> buscarPecasComEstoqueInsuficiente(List<ItemOSProduto> itemOSProdutos);
}
