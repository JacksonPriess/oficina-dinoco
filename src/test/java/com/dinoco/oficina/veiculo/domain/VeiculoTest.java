package com.dinoco.oficina.veiculo.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VeiculoTest {

    @Test
    void deveCriarVeiculoComDadosValidos() {
        Veiculo veiculo = new Veiculo("ABC1234", "Toyota", "Corolla", 2020, 2021, "Prata", "12345678901234567", "1.8");

        assertNotNull(veiculo);
        assertEquals("ABC1234", veiculo.getPlaca());
        assertEquals("Toyota", veiculo.getMarca());
        assertEquals("Corolla", veiculo.getModelo());
        assertEquals(2020, veiculo.getAnoFabricacao());
        assertEquals(2021, veiculo.getAnoModelo());
        assertEquals("Prata", veiculo.getCor());
        assertEquals("12345678901234567", veiculo.getChassi());
        assertEquals("1.8", veiculo.getMotor());
        assertTrue(veiculo.getAtivo());
        assertNotNull(veiculo.getDataCriacao());
    }

    @Test
    void deveLancarExcecaoAoCriarVeiculoComPlacaInvalida() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Veiculo("INVALIDA", "Toyota", "Corolla", 2020, 2021, "Prata", "12345678901234567", "1.8");
        });

        assertEquals("Placa inválida. Digite apenas letras e números no padrão antigo (ABC1234) ou mercosul (ABC1D23), sem hífen.", exception.getMessage());
    }

}