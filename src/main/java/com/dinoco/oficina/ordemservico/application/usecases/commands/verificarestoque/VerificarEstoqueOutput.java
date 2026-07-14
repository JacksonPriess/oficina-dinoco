package com.dinoco.oficina.ordemservico.application.usecases.commands.verificarestoque;

import com.dinoco.oficina.ordemservico.infrastructure.web.dto.PecaPendenteDto;
import java.util.List;

public record VerificarEstoqueOutput(Long osId,
                                     String statusOS,
                                     boolean prontaParaExecucao,
                                     List<PecaPendenteDto> pecasFaltantes)
{}

