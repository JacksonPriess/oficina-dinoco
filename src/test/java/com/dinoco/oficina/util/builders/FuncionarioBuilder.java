package com.dinoco.oficina.util.builders;

import com.dinoco.oficina.entity.Funcionario;
import com.dinoco.oficina.enums.CargoFuncionario;

public class FuncionarioBuilder {

    private Funcionario funcionario;

    private FuncionarioBuilder() {
        this.funcionario = new Funcionario();
    }

    public static FuncionarioBuilder umFuncionario() {
        FuncionarioBuilder builder = new FuncionarioBuilder();
        builder.funcionario.setId(1L);
        builder.funcionario.setNome("Mecânico Silva");
        builder.funcionario.setCpf("52998224725");
        builder.funcionario.setCargo(CargoFuncionario.MECANICO);
        builder.funcionario.setAtivo(true);
        builder.funcionario.setUsuarioId(null);
        return builder;
    }

    public FuncionarioBuilder comId(Long id) {
        this.funcionario.setId(id);
        return this;
    }

    public FuncionarioBuilder comCpf(String cpf) {
        this.funcionario.setCpf(cpf);
        return this;
    }

    public FuncionarioBuilder comUsuarioId(Long usuarioId) {
        this.funcionario.setUsuarioId(usuarioId);
        return this;
    }

    public Funcionario build() {
        return this.funcionario;
    }
}
