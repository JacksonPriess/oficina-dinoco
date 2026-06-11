package com.dinoco.oficina.cliente.infrastructure.web.dto;

public record EnderecoDto(
    String cep,
    String logradouro,
    String numero,
    String complemento,
    String bairro,
    String cidade,
    String uf
) {}

