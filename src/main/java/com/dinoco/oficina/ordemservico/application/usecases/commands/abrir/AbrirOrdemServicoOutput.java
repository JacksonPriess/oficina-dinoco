package com.dinoco.oficina.ordemservico.application.usecases.commands.abrir;

import com.dinoco.oficina.ordemservico.domain.enums.StatusOS;
import java.math.BigDecimal;

public record AbrirOrdemServicoOutput(
        Long osId,
        String codigoRastreio,
        Long clienteId,
        Long veiculoId,
        StatusOS status,
        String reclamacaoCliente,
        Integer quilometragemEntrada,
        BigDecimal valorTotalServicos,
        BigDecimal valorTotalProdutos,
        BigDecimal valorTotalOS,
        BigDecimal valorDesconto
) {}