package com.dinoco.oficina.ordemservico.application.usecases.commands.verificarestoque;

import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.ordemservico.application.gateways.VerificadorEstoqueGateway;
import com.dinoco.oficina.ordemservico.domain.enums.StatusOS;
import com.dinoco.oficina.ordemservico.domain.exceptions.RecursoNaoEncontradoException;
import com.dinoco.oficina.ordemservico.domain.exceptions.RegraNegocioOSException;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import com.dinoco.oficina.ordemservico.infrastructure.web.dto.PecaPendenteDto;
import java.util.List;

public class VerificarEstoqueHandler implements VerificarEstoqueUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;
    private final VerificadorEstoqueGateway verificadorEstoqueGateway;

    public VerificarEstoqueHandler(VerificadorEstoqueGateway verificadorEstoqueGateway,
                                   OrdemServicoCommandGateway ordemServicoCommandGateway) {
        this.verificadorEstoqueGateway = verificadorEstoqueGateway;
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
    }

    @Override
    public VerificarEstoqueOutput executar(VerificarEstoqueCommand command) {
        OrdemServico ordemServico = ordemServicoCommandGateway.buscarParaAlteracao(command.osId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada."));

        if (ordemServico.getStatus() != StatusOS.AGUARDANDO_FORNECEDOR) {
            throw new RegraNegocioOSException("A verificação de estoque só pode ser realizada quando a OS estiver AGUARDANDO_FORNECEDOR.");
        }

        if (ordemServico.getItensProduto() == null || ordemServico.getItensProduto().isEmpty()) {
            throw new RegraNegocioOSException("Falha de integridade: Não é possível verificar estoque para uma OS sem produtos.");
        }

        List<PecaPendenteDto> pecasComEstoqueInsuficiente = verificadorEstoqueGateway.
                buscarPecasComEstoqueInsuficiente(ordemServico.getItensProduto());

        if ( pecasComEstoqueInsuficiente.isEmpty() ) {
            ordemServico.marcarProntaParaExecucao(); // Vai para AGUARDANDO_EXECUCAO
            ordemServicoCommandGateway.salvar(ordemServico);
        }


        // Retorna o Output rico para a Controller
        return new VerificarEstoqueOutput(
                ordemServico.getId(),
                ordemServico.getStatus().name(),
                pecasComEstoqueInsuficiente.isEmpty(),
                pecasComEstoqueInsuficiente
        );
    }
}