package com.dinoco.oficina.cliente.application.usecases.commands.atualizar;

import java.util.List;

public record AtualizarClienteCommand(
        Long id,
        String tipoPessoa,
        String documento,
        String inscricaoEstadual,
        String nome,
        String nomeFantasia,
        String email,
        String telefone,
        List<EnderecoCommand> enderecos
) {}
