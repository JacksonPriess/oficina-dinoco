package com.dinoco.oficina.util.builders;

import com.dinoco.oficina.dto.ServicoRequestDto;
import java.math.BigDecimal;

public class ServicoRequestDtoBuilder {

    private ServicoRequestDtoBuilder() {}

    public static ServicoRequestDto umRequest() {
        return new ServicoRequestDto(
                "Alinhamento e Balanceamento",
                new BigDecimal("120.00"),
                60
        );
    }

    public static ServicoRequestDto umRequestComNovaDescricao(String novaDescricao) {
        return new ServicoRequestDto(
                novaDescricao,
                new BigDecimal("150.00"),
                90
        );
    }
}