package com.dinoco.oficina.ordemservico.application.usecases.queries.buscarpornumero;

public record BuscarOSPorCodigoRastreioQuery(
        String codigoRastreio,
        Long clienteId
) {}
