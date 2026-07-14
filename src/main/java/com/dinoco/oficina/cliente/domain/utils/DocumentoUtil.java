package com.dinoco.oficina.cliente.domain.utils;

public class DocumentoUtil {
    public static boolean isCpfValido(String cpf) {
        if (cpf == null || cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false; // Rejeita CPFs com todos os números iguais (ex: 11111111111)
        }

        try {
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += (cpf.charAt(i) - '0') * (10 - i);
            }
            int peso1 = 11 - (soma % 11);
            int digito1 = (peso1 > 9) ? 0 : peso1;

            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += (cpf.charAt(i) - '0') * (11 - i);
            }
            int peso2 = 11 - (soma % 11);
            int digito2 = (peso2 > 9) ? 0 : peso2;

            return (cpf.charAt(9) - '0' == digito1) && (cpf.charAt(10) - '0' == digito2);
        } catch (Exception e) {
            return false; // Se tiver alguma letra perdida no meio, falha
        }
    }

    public static boolean isCnpjValido(String cnpj) {
        if (cnpj == null || cnpj.length() != 14) {
            return false;
        }

        // Converte para maiúsculas para garantir que o cálculo ASCII funcione corretamente
        cnpj = cnpj.toUpperCase();

        int[] peso1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] peso2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        try {
            int soma = 0;
            // Cálculo do 1º dígito verificador (utiliza os 12 primeiros caracteres)
            for (int i = 0; i < 12; i++) {
                // char - 48 pega o valor correto tanto para número quanto para letra
                int valorAscii = cnpj.charAt(i) - 48;
                soma += valorAscii * peso1[i];
            }
            int resto = soma % 11;
            int digito1 = (resto < 2) ? 0 : (11 - resto);

            soma = 0;
            // Cálculo do 2º dígito verificador (utiliza os 13 primeiros caracteres)
            for (int i = 0; i < 13; i++) {
                int valorAscii = cnpj.charAt(i) - 48;
                soma += valorAscii * peso2[i];
            }
            resto = soma % 11;
            int digito2 = (resto < 2) ? 0 : (11 - resto);

            // Os dois últimos dígitos são sempre números
            return (cnpj.charAt(12) - '0' == digito1) && (cnpj.charAt(13) - '0' == digito2);
        } catch (Exception e) {
            return false;
        }
    }
}
