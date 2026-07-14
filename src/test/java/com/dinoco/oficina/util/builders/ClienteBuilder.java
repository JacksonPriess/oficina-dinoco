package com.dinoco.oficina.util.builders;

import com.dinoco.oficina.cliente.infrastructure.persistence.ClienteEntity;
import com.dinoco.oficina.cliente.infrastructure.persistence.EnderecoEntity;

import java.time.LocalDateTime;

public class ClienteBuilder {

    private ClienteEntity cliente;

    private ClienteBuilder() {
        this.cliente = new ClienteEntity();
    }

    public static ClienteBuilder umClientePF() {
        ClienteBuilder builder = new ClienteBuilder();
        builder.cliente.setId(1L);
        builder.cliente.setTipoPessoa("F");
        builder.cliente.setDocumento("12345678909");
        builder.cliente.setNome("João da Silva");
        builder.cliente.setInscricaoEstadual(null);
        builder.cliente.setNomeFantasia(null);
        builder.cliente.setEmail("joao.silva@email.com");
        builder.cliente.setTelefone("47987654321");
        builder.cliente.setAtivo(true);
        builder.cliente.setDataCriacao(LocalDateTime.now());
        builder.cliente.addEndereco(EnderecoBuilder.umEndereco().build());

        return builder;
    }

    public static ClienteBuilder umClientePJ() {
        ClienteBuilder builder = new ClienteBuilder();
        builder.cliente.setId(1L);
        builder.cliente.setTipoPessoa("J");
        builder.cliente.setDocumento("19131243000197");
        builder.cliente.setNome("Empresa Teste");
        builder.cliente.setInscricaoEstadual("12345678912345678925");
        builder.cliente.setNomeFantasia("Empresa Teste Fantasia");
        builder.cliente.setEmail("empresa.teste@email.com");
        builder.cliente.setTelefone("47987654322");
        builder.cliente.setAtivo(true);
        builder.cliente.setDataCriacao(LocalDateTime.now());
        builder.cliente.addEndereco(EnderecoBuilder.umEndereco().build());
        return builder;
    }

    public ClienteBuilder comId(Long id) {
        this.cliente.setId(id);
        return this;
    }

    public ClienteBuilder comTipoPessoa(String tipoPessoa) {
        this.cliente.setTipoPessoa(tipoPessoa);
        return this;
    }

    public ClienteBuilder comDocumento(String documento) {
        this.cliente.setDocumento(documento);
        return this;
    }

    public ClienteBuilder comNome(String nome) {
        this.cliente.setNome(nome);
        return this;
    }

    public ClienteBuilder comInscricaoEstadual(String inscricaoEstadual) {
        this.cliente.setInscricaoEstadual(inscricaoEstadual);
        return this;
    }

    public ClienteBuilder comNomeFantasia(String nomeFantasia) {
        this.cliente.setNomeFantasia(nomeFantasia);
        return this;
    }

    public ClienteBuilder comEmail(String email) {
        this.cliente.setEmail(email);
        return this;
    }

    public ClienteBuilder comTelefone(String telefone) {
        this.cliente.setTelefone(telefone);
        return this;
    }

    public ClienteBuilder comDataCriacao(LocalDateTime dataCriacao) {
        this.cliente.setDataCriacao(dataCriacao);
        return this;
    }

    public ClienteBuilder comEndereco(EnderecoEntity endereco) {
        this.cliente.addEndereco(endereco);
        return this;
    }

    public ClienteEntity build() {
        return this.cliente;
    }
}