package com.dinoco.oficina.cliente.application.usecases.queries.buscarporid;

import java.time.LocalDateTime;
import java.util.List;

public record BuscarClientePorIdOutput(
        Long id,
        String tipoPessoa,
        String documento,
        String nome,
        String inscricaoEstadual,
        String nomeFantasia,
        String email,
        String telefone,
        Boolean ativo,
        LocalDateTime dataCriacao,
        List<EnderecoQueryOutput> enderecos
) {}
