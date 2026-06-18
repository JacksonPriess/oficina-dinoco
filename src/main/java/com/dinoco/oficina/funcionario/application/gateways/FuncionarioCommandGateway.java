package com.dinoco.oficina.funcionario.application.gateways;

import com.dinoco.oficina.funcionario.domain.Funcionario;
import java.util.Optional;

public interface FuncionarioCommandGateway {

    Funcionario salvar(Funcionario funcionario);

    Optional<Funcionario> buscarParaAlteracao(Long id);
}