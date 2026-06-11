package com.dinoco.oficina.metrica.application.usecase.queries.buscarmediaexecucao;

import java.math.BigDecimal;

public record BuscarMediaExecucaoServicosDetalhesOutput(
        Long codigoServico,
        String descricao,
        Long quantidadeExecutada,
        BigDecimal mediaMinutos,
        Integer tempoPadraoMinutos,
        BigDecimal desvioAbsolutoMinutos,
        BigDecimal percentualDiferenca)
{}