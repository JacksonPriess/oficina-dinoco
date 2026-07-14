package com.dinoco.oficina.shared.events;

import com.dinoco.oficina.ordemservico.domain.models.ItemOSProduto;
import java.util.List;

public record OrcamentoAprovadoEvent(
        Long osId, List<ItemOSProduto> itensOSProduto )
{}