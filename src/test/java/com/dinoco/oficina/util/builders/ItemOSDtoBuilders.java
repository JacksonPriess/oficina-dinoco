package com.dinoco.oficina.util.builders;

import com.dinoco.oficina.dto.ItemOSProdutoAdicionarDto;
import com.dinoco.oficina.dto.ItemOSProdutoAlterarDto;
import com.dinoco.oficina.dto.ItemOSServicoAdicionarDto;
import com.dinoco.oficina.dto.ItemOSServicoAlterarDto;

import java.math.BigDecimal;

public class ItemOSDtoBuilders {

    public static ItemOSServicoAdicionarDto adicionarServicoDto(Long servicoId, Long mecanicoId) {
        return new ItemOSServicoAdicionarDto(servicoId, mecanicoId);
    }

    public static ItemOSServicoAlterarDto alterarServicoDto(BigDecimal valorCobrado, Long mecanicoId) {
        return new ItemOSServicoAlterarDto(valorCobrado, mecanicoId);
    }

    public static ItemOSProdutoAdicionarDto adicionarProdutoDto(Long produtoId, BigDecimal quantidade) {
        return new ItemOSProdutoAdicionarDto(produtoId, quantidade);
    }

    public static ItemOSProdutoAlterarDto alterarProdutoDto(BigDecimal quantidade, BigDecimal valorUnitario) {
        return new ItemOSProdutoAlterarDto(quantidade, valorUnitario);
    }
}
