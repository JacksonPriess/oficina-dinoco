package com.dinoco.oficina.util.builders;

import com.dinoco.oficina.dto.FuncionarioRequestDto;

public class FuncionarioRequestDtoBuilder {

    private FuncionarioRequestDtoBuilder() {}

    public static FuncionarioRequestDto criarSemAcesso(String cpf) {
        return new FuncionarioRequestDto(
                "Mecânico Silva",
                cpf,
                "Mecânico Sênior",
                false,
                null,
                null
        );
    }

    public static FuncionarioRequestDto criarComAcesso(String cpf, String login, String senha) {
        return new FuncionarioRequestDto(
                "Mecânico Silva",
                cpf,
                "Mecânico Sênior",
                true,
                login,
                senha
        );
    }
}