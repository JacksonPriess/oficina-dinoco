package com.dinoco.oficina.funcionario.domain;

import com.dinoco.oficina.funcionario.domain.utils.DocumentoUtil;
import lombok.Data;

@Data
public class Funcionario {

    private Long id;
    private String nome;
    private String cpf;
    private CargoFuncionario cargo;
    private boolean ativo;
    private Long usuarioId;


    // Construtor para criação de um NOVO funcionário (Regras de negócio aplicadas aqui)
    public Funcionario(String nome, String cpf, CargoFuncionario cargo) {
        if (!DocumentoUtil.isCpfValido(cpf)) {
            throw new IllegalArgumentException("CPF inválido.");
        }
        this.nome = nome;
        this.cpf = cpf;
        this.cargo = cargo;
        this.ativo = true;
    }

    // Construtor para reconstruir o funcionário vindo do banco de dados
    public Funcionario(Long id, String nome, String cpf, CargoFuncionario cargo, boolean ativo, Long usuarioId) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.cargo = cargo;
        this.ativo = ativo;
        this.usuarioId = usuarioId;
    }

    // Regra de negócio isolada
    public PerfilUsuario definirPerfilAcesso() {
        return switch (this.cargo) {
            case MECANICO -> PerfilUsuario.MECANICO;
            case ATENDENTE -> PerfilUsuario.ATENDENTE;
        };
    }

    // Métodos de negócio
    public void vincularUsuario(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void desativar() {
        this.ativo = false;
    }

        public void atualizarDados(String nome, CargoFuncionario cargo ) {
        this.nome = nome;
        this.cargo = cargo;
    }
}
