package com.dinoco.oficina.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DocumentoUtilTest {

    @Test
    void deveValidarCpfCorreto() {
        assertTrue(DocumentoUtil.isCpfValido("52998224725"));
        assertTrue(DocumentoUtil.isCpfValido("00000000191"));
    }

    @Test
    void deveRejeitarCpfInvalidoETamanhoIncorreto() {
        assertFalse(DocumentoUtil.isCpfValido("52998224726"), "Dígito errado");
        assertFalse(DocumentoUtil.isCpfValido("11111111111"), "Tamanho errado");
        assertFalse(DocumentoUtil.isCpfValido("123"), "Tamanho errado");
        assertFalse(DocumentoUtil.isCpfValido(null), "Nulo");
    }

    @Test
    void deveValidarCnpjNumericoAntigo() {
        assertTrue(DocumentoUtil.isCnpjValido("19131243000197"));
        assertTrue(DocumentoUtil.isCnpjValido("00000000000191"));
    }

    @Test
    void deveValidarCnpjAlfanumericoNovo() {
        // O novo formato da Receita Federal com letras (Válido matematicamente)
        assertTrue(DocumentoUtil.isCnpjValido("12ABC34501DE35"));
    }

    @Test
    void deveRejeitarCnpjInvalido() {
        assertFalse(DocumentoUtil.isCnpjValido("19131243000198")); // Dígito errado
        assertFalse(DocumentoUtil.isCnpjValido("12ABC345"));       // Tamanho errado
    }
}