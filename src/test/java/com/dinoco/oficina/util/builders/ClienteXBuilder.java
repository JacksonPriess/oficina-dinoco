package com.dinoco.oficina.util.builders;

import com.dinoco.oficina.cliente.domain.Cliente;

import java.time.LocalDateTime;

public class ClienteXBuilder {

    private Cliente cliente;

    private ClienteXBuilder() {
        this.cliente = new Cliente();
    }

    public static ClienteXBuilder umClientePF() {
        ClienteXBuilder builder = new ClienteXBuilder();
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
        builder.cliente.adicionarEndereco(EnderecoXBuilder.umEndereco().build());

        return builder;
    }

    public static ClienteXBuilder umClientePJ() {
        ClienteXBuilder builder = new ClienteXBuilder();
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
        builder.cliente.adicionarEndereco(EnderecoXBuilder.umEndereco().build());
        return builder;
    }

    public Cliente build() {
        return this.cliente;
    }
}