package com.dinoco.oficina.util.builders;

import com.dinoco.oficina.cliente.infrastructure.web.dto.ClienteRequestDto;
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
                Collections.singletonList(EnderecoDtoBuilder.umEnderecoDto().build())
        );
    }

    public static ClienteRequestDto criarPessoaJuridica(String documento) {
        return new ClienteRequestDto(
                "J", documento,
                "12345678912345678925",
                "Empresa Teste",
                "Empresa Teste Fantasia",
                "empresa.teste@email.com",
                "47987654322",
                Collections.singletonList(EnderecoDtoBuilder.umEnderecoDto().build())
        );
    }
}
