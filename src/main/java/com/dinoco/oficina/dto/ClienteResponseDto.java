package com.dinoco.oficina.dto;

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
    List<EnderecoDto> enderecos
) {}
