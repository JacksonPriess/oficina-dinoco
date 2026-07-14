package com.dinoco.oficina.cliente.application.usecases.commands.atualizar;

import java.util.List;

public record AtualizarClienteOutput(
        Long id,
        String tipoPessoa,
        String documento,
        String inscricaoEstadual,
        String nome,
        String nomeFantasia,
        String email,
        String telefone,
        Boolean ativo,
        List<EnderecoOutput> enderecos
) {}
