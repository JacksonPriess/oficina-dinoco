package com.dinoco.oficina.ordemservico.application.usecases.commands.concluirdiagnostico;

public record ConcluirDiagnosticoCommand(
        Long osId,
        String laudo) {}