package com.dinoco.oficina.util.builders;


import com.dinoco.oficina.ordemservico.infrastructure.persistence.OrdemServicoEntity;

public class OrdemServicoBuilder {
    private Integer quilometragemEntrada = 85000;
    private String reclamacaoCliente = "Barulho no motor";

    public static OrdemServicoBuilder umaOrdemServico() {
        return new OrdemServicoBuilder();
    }

    public OrdemServicoEntity build() {
        return null;

    }
}
