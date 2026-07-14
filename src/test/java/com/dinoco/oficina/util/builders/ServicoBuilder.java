package com.dinoco.oficina.util.builders;


import com.dinoco.oficina.catalogoservico.domain.Servico;

import java.math.BigDecimal;

public class ServicoBuilder {

    private Servico servico;

    private ServicoBuilder() {

    }

    public static ServicoBuilder umServico() {
        ServicoBuilder builder = new ServicoBuilder();
        builder.servico.setId(1L);
        builder.servico.setDescricao("Troca de Óleo");
        builder.servico.setPrecoPadrao(new BigDecimal("150.00"));
        builder.servico.setTempoEstimadoMinutos(60);
        return builder;
    }

    public ServicoBuilder comId(Long id) {
        this.servico.setId(id);
        return this;
    }

    public ServicoBuilder comDescricao(String descricao) {
        this.servico.setDescricao(descricao);
        return this;
    }

    public ServicoBuilder comPrecoPadrao(BigDecimal preco) {
        this.servico.setPrecoPadrao(preco);
        return this;
    }

    public Servico build() {
        return this.servico;
    }
}