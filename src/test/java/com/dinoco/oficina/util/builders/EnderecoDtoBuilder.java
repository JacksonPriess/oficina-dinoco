package com.dinoco.oficina.util.builders;

import com.dinoco.oficina.dto.EnderecoDto;

public class EnderecoDtoBuilder {

    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;

    private EnderecoDtoBuilder() {}

    public static EnderecoDtoBuilder umEnderecoDto() {
        EnderecoDtoBuilder builder = new EnderecoDtoBuilder();
        builder.cep = "89200-000";
        builder.logradouro = "Rua das Flores";
        builder.numero = "123";
        builder.complemento = "Apto 101";
        builder.bairro = "Centro";
        builder.cidade = "Joinville";
        builder.uf = "SC";
        return builder;
    }

    public EnderecoDtoBuilder comCep(String cep) {
        this.cep = cep;
        return this;
    }

    public EnderecoDtoBuilder comLogradouro(String logradouro) {
        this.logradouro = logradouro;
        return this;
    }

    public EnderecoDtoBuilder comNumero(String numero) {
        this.numero = numero;
        return this;
    }

    public EnderecoDtoBuilder comComplemento(String complemento) {
        this.complemento = complemento;
        return this;
    }

    public EnderecoDtoBuilder comBairro(String bairro) {
        this.bairro = bairro;
        return this;
    }

    public EnderecoDtoBuilder comCidade(String cidade) {
        this.cidade = cidade;
        return this;
    }

    public EnderecoDtoBuilder comUf(String uf) {
        this.uf = uf;
        return this;
    }

    public EnderecoDto build() {

        return new EnderecoDto(
                this.cep,
                this.logradouro,
                this.numero,
                this.complemento,
                this.bairro,
                this.cidade,
                this.uf
        );
    }
}
