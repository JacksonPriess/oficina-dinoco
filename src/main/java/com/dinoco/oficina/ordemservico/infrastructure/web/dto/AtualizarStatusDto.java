package com.dinoco.oficina.ordemservico.infrastructure.web.dto;

public record AtualizarStatusDto(
        AcaoStatus acao,
        String laudo ) {
}
