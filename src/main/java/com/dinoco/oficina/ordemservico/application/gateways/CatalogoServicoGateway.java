package com.dinoco.oficina.ordemservico.application.gateways;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;
import java.util.Map;

public interface CatalogoServicoGateway {
    Map<Long, ServicoCatalogo> buscarDetalhes(List<Long> ids);

    record ServicoCatalogo(Long id, String descricao, BigDecimal precoPadrao, Integer tempoEstimadoMinutos) {}

    Optional<BigDecimal> buscarPrecoPadrao(Long servicoId);
}
