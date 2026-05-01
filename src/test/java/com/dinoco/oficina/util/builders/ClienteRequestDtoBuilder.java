package com.dinoco.oficina.util.builders;

import com.dinoco.oficina.dto.ClienteRequestDto;
import java.util.Collections;

public abstract class ClienteRequestDtoBuilder {

    public static ClienteRequestDto criarPessoaFisica(String documento) {
        return new ClienteRequestDto(
                "F", documento,
                null,
                "João da Silva",
                null,
                "joao@email.com",
                "4799999999",
                Collections.emptyList()
        );
    }
}
