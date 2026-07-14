package com.dinoco.oficina.cliente.application.usecases.commands.criar;

public record EnderecoOutput(String cep, String logradouro, String numero, String complemento, String bairro, String cidade, String uf) {}