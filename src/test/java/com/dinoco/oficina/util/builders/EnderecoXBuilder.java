package com.dinoco.oficina.util.builders;

import com.dinoco.oficina.cliente.domain.Endereco;

public class EnderecoXBuilder {

    private Endereco endereco;

    private EnderecoXBuilder() {
        this.endereco = new Endereco();
    }

    public static EnderecoXBuilder umEndereco() {
        EnderecoXBuilder builder = new EnderecoXBuilder();

        // Dados válidos de exemplo
        builder.endereco.setCep("89200-000");
        builder.endereco.setLogradouro("Rua das Flores");
        builder.endereco.setNumero("123");
        builder.endereco.setComplemento("Apto 101");
        builder.endereco.setBairro("Centro");
        builder.endereco.setCidade("Joinville");
        builder.endereco.setUf("SC");
        return builder;
    }

    public Endereco build() {
        return this.endereco;
    }
}