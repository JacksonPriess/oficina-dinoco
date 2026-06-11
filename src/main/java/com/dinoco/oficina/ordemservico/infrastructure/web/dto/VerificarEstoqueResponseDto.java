package com.dinoco.oficina.ordemservico.infrastructure.web.dto;

import java.util.List;

public record VerificarEstoqueResponseDto(
        Long osId,
        String statusOS,
        boolean prontaParaExecucao,
        List<PecaPendenteDto> pecasPendentes
) {}
