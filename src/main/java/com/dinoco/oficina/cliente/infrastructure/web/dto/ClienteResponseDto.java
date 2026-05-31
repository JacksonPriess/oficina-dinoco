package com.dinoco.oficina.cliente.infrastructure.web.dto;

import com.dinoco.oficina.dto.EnderecoDto;

import java.util.List;

public record ClienteResponseDto(
    Long id,
    String tipoPessoa,
    String documento,
    String inscricaoEstadual,
    String nome,
    String nomeFantasia,
    String email,
    String telefone,
    Boolean ativo,
    List<EnderecoDto> enderecos
) {}
