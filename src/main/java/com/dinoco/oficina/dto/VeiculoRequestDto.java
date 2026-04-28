package com.dinoco.oficina.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VeiculoRequestDto(

        @NotBlank(message = "A placa não pode estar vazia.")
        @Pattern(regexp = "^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$",
                message = "Placa inválida. Digite apenas letras e números no padrão antigo (ABC1234) ou mercosul (ABC1D23), sem hífen.")
        String placa,

        @NotBlank(message = "A marca do veículo é obrigatória.")
        String marca,

        @NotBlank(message = "O modelo do veículo é obrigatório.")

        String modelo,
        Integer anoFabricacao,
        Integer anoModelo,
        String cor,
        String chassi,
        String motor
) {}