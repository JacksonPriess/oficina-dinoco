package com.dinoco.oficina.cliente.application.usecases.commands.criar;

import java.util.List;

public record CriarClienteCommand(
        String tipoPessoa,
        String documento,
        String inscricaoEstadual,
        String nome,
        String nomeFantasia,
        String email,
        String telefone,
        List<EnderecoCommand> enderecos
) {}
