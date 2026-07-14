package com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemservico;

import com.dinoco.oficina.ordemservico.application.gateways.CatalogoServicoGateway;
import com.dinoco.oficina.ordemservico.application.gateways.OrdemServicoCommandGateway;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.ordemservico.domain.models.ItemOSServico;
import com.dinoco.oficina.ordemservico.domain.models.OrdemServico;
import java.math.BigDecimal;

public class AdicionarItemServicoHandler implements AdicionarItemServicoUseCase {

    private final OrdemServicoCommandGateway ordemServicoCommandGateway;
    private final CatalogoServicoGateway catalogoServicoGateway;

    public AdicionarItemServicoHandler(OrdemServicoCommandGateway ordemServicoCommandGateway, CatalogoServicoGateway catalogoServicoGateway) {
        this.ordemServicoCommandGateway = ordemServicoCommandGateway;
        this.catalogoServicoGateway = catalogoServicoGateway;
    }

    @Override
    public void executar(AdicionarItemServicoCommand command) {
        OrdemServico ordemServico = ordemServicoCommandGateway.buscarParaAlteracao(command.osId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada."));

        BigDecimal precoPadrao = catalogoServicoGateway.buscarPrecoPadrao(command.servicoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado no catálogo."));
        ItemOSServico novoServico = new ItemOSServico(command.servicoId(), command.mecanicoId(), precoPadrao);
        ordemServico.adicionarServico(novoServico);
        ordemServicoCommandGateway.salvar(ordemServico);
    }
}
