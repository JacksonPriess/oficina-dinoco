package com.dinoco.oficina.cliente.domain;

import com.dinoco.oficina.cliente.domain.utils.DocumentoUtil;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Cliente {

    private Long id;
    private final String tipoPessoa;
    private final String documento;
    private final LocalDateTime dataCriacao;
    private String nome;
    private String inscricaoEstadual;
    private String nomeFantasia;
    private String email;
    private String telefone;
    private Boolean ativo;

    private final List<Endereco> enderecos;

    public Cliente(String tipoPessoa, String documento, String nome, String inscricaoEstadual,
                   String nomeFantasia, String email, String telefone) {

        validarDocumento(tipoPessoa, documento);

        this.tipoPessoa = tipoPessoa;
        this.documento = documento;
        this.nome = nome;
        this.inscricaoEstadual = inscricaoEstadual;
        this.nomeFantasia = nomeFantasia;
        this.email = email;
        this.telefone = telefone;
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
        this.enderecos = new ArrayList<>();
    }

    public Cliente(Long id, String tipoPessoa, String documento, String nome, String inscricaoEstadual,
                   String nomeFantasia, String email, String telefone, Boolean ativo,
                   LocalDateTime dataCriacao, List<Endereco> enderecos) {
        this.id = id;
        this.tipoPessoa = tipoPessoa;
        this.documento = documento;
        this.nome = nome;
        this.inscricaoEstadual = inscricaoEstadual;
        this.nomeFantasia = nomeFantasia;
        this.email = email;
        this.telefone = telefone;
        this.ativo = ativo;
        this.dataCriacao = dataCriacao;
        this.enderecos = enderecos != null ? new ArrayList<>(enderecos) : new ArrayList<>();
    }

    // --- REGRAS DE NEGÓCIO E COMPORTAMENTOS ---

    private void validarDocumento(String tipoPessoa, String documento) {
        if ("F".equals(tipoPessoa) && !DocumentoUtil.isCpfValido(documento)) {
            throw new IllegalArgumentException("CPF inválido.");
        }
        if ("J".equals(tipoPessoa) && !DocumentoUtil.isCnpjValido(documento)) {
            throw new IllegalArgumentException("CNPJ inválido.");
        }
    }

    public void desativar() {
        this.ativo = false;
    }

    public void adicionarEndereco(Endereco endereco) {
        if (endereco != null) {
            this.enderecos.add(endereco);
        }
    }

    public void atualizarDados(String nome, String inscricaoEstadual, String nomeFantasia,
                               String email, String telefone) {
        this.nome = nome;
        this.inscricaoEstadual = inscricaoEstadual;
        this.nomeFantasia = nomeFantasia;
        this.email = email;
        this.telefone = telefone;
    }

    public void substituirEnderecos(List<Endereco> novosEnderecos) {
        this.enderecos.clear();
        if (novosEnderecos != null) {
            this.enderecos.addAll(novosEnderecos);
        }
    }
}
