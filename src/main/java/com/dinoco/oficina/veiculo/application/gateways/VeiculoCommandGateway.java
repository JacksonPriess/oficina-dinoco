package com.dinoco.oficina.veiculo.application.gateways;

import com.dinoco.oficina.veiculo.domain.Veiculo;
import java.util.Optional;

public interface VeiculoCommandGateway {

    Veiculo salvar(Veiculo veiculo);

    Optional<Veiculo> buscarParaAlteracao(Long id);
}