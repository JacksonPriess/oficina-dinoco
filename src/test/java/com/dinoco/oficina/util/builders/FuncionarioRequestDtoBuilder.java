package com.dinoco.oficina.util.builders;

import com.dinoco.oficina.dto.FuncionarioRequestDto;
import com.dinoco.oficina.enums.CargoFuncionario;

public class FuncionarioRequestDtoBuilder {

    private FuncionarioRequestDtoBuilder() {}

    public static FuncionarioRequestDto criarSemAcesso(String cpf) {
        return new FuncionarioRequestDto(
                "Mecânico Silva",
                cpf,
                CargoFuncionario.MECANICO,
                false,
                null,
                null
        );
    }

    public static FuncionarioRequestDto criarComAcesso(String cpf, String login, String senha) {
        return new FuncionarioRequestDto(
                "Mecânico Silva",
                cpf,
                CargoFuncionario.MECANICO,
                true,
                login,
                senha
        );
    }
}