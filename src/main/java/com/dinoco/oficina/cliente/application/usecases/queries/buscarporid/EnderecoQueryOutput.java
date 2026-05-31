package com.dinoco.oficina.cliente.application.usecases.queries.buscarporid;

public record EnderecoQueryOutput(
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String uf
) {}