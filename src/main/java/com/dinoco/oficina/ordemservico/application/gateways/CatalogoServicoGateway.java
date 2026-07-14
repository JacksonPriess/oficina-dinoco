package com.dinoco.oficina.ordemservico.application.gateways;

import java.math.BigDecimal;
import java.util.Optional;

public interface CatalogoServicoGateway {
    Optional<BigDecimal> buscarPrecoPadrao(Long servicoId);
}
