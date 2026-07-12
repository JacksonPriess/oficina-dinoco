package com.dinoco.oficina.ordemservico.application.gateways;

import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import java.util.Optional;

public interface OrdemServicoCommandGateway {

    OrdemServico salvar(OrdemServico ordemServico);

    Optional<OrdemServico> buscarParaAlteracao(Long id);

    Optional<OrdemServico> buscarPorCodigoRastreioParaAlteracao(String codigoRastreio);

}