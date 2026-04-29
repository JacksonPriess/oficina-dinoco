package com.dinoco.oficina.helper;

import com.dinoco.oficina.entity.OrdemServico;

public class OrdemServicoBuilder {
    private Integer quilometragemEntrada = 85000;
    private String reclamacaoCliente = "Barulho no motor";

    public static OrdemServicoBuilder umaOrdemServico() {
        return new OrdemServicoBuilder();
    }

    public OrdemServico build() {
        return new OrdemServico(
                ClienteBuilder.umCliente(),
                VeiculoBuilder.umVeiculo(),
                quilometragemEntrada,
                reclamacaoCliente
        );
    }
}
