package com.dinoco.oficina.cliente.application.gateways;

import com.dinoco.oficina.cliente.domain.Cliente;
import java.util.Optional;

public interface ClienteCommandGateway {

    Cliente salvar(Cliente cliente);

    Optional<Cliente> buscarParaAlteracao(Long id);
}