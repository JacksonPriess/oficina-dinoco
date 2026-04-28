package com.dinoco.oficina.dto;

public record DetalheMetricaServicoDTO(
        Long codigoServico,
        String descricao,
        Long quantidadeExecutada,
        Double mediaMinutos,
        Integer tempoPadrao,
        Double desvioAbsolutoMinutos,
        Double percentualDiferenca
) {}
