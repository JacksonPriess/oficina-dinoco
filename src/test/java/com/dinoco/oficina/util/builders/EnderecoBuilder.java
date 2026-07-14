package com.dinoco.oficina.util.builders;

import com.dinoco.oficina.cliente.infrastructure.persistence.EnderecoEntity;

public class EnderecoBuilder {

    private EnderecoEntity endereco;

    private EnderecoBuilder() {
        this.endereco = new EnderecoEntity();
    }

    public static EnderecoBuilder umEndereco() {
        EnderecoBuilder builder = new EnderecoBuilder();

        // Dados válidos de exemplo
        builder.endereco.setId(1L);
        builder.endereco.setCep("89200-000");
        builder.endereco.setLogradouro("Rua das Flores");
        builder.endereco.setNumero("123");
        builder.endereco.setComplemento("Apto 101");
        builder.endereco.setBairro("Centro");
        builder.endereco.setCidade("Joinville");
        builder.endereco.setUf("SC");
        return builder;
    }

    public EnderecoBuilder comId(Long id) {
        this.endereco.setId(id);
        return this;
    }

    public EnderecoBuilder comCep(String cep) {
        this.endereco.setCep(cep);
        return this;
    }

    public EnderecoBuilder comLogradouro(String logradouro) {
        this.endereco.setLogradouro(logradouro);
        return this;
    }

    public EnderecoBuilder comNumero(String numero) {
        this.endereco.setNumero(numero);
        return this;
    }

    public EnderecoBuilder comComplemento(String complemento) {
        this.endereco.setComplemento(complemento);
        return this;
    }

    public EnderecoBuilder comBairro(String bairro) {
        this.endereco.setBairro(bairro);
        return this;
    }

    public EnderecoBuilder comCidade(String cidade) {
        this.endereco.setCidade(cidade);
        return this;
    }

    public EnderecoBuilder comUf(String uf) {
        this.endereco.setUf(uf);
        return this;
    }

    public EnderecoEntity build() {
        return this.endereco;
    }
}