package com.dinoco.oficina.shared.events;

import com.dinoco.oficina.ordemservico.domain.models.ItemOSProduto;
import java.util.List;

public record ExecucaoIniciadaEvent(
        Long osId, List<ItemOSProduto> itensOSProduto )
{}

