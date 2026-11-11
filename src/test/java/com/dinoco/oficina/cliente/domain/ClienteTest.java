package com.dinoco.oficina.cliente.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.val;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    @Test
    void deveCriarClienteComCPFValido() {
        val cliente = criarClientePF("52998224725");
        assertEquals("João Silva", cliente.getNome());
        assertEquals("52998224725", cliente.getDocumento());
        assertEquals("F", cliente.getTipoPessoa());
        assertNull(cliente.getInscricaoEstadual());
        assertNull(cliente.getNomeFantasia());
        assertEquals("47988774455", cliente.getTelefone());
        assertEquals("joao@gmail.com", cliente.getEmail());
        assertTrue(cliente.getAtivo());
        assertNotNull(cliente.getDataCriacao());
    }

    @Test
    void deveLancarExcecaoQuandoCPFForInvalido() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> criarClientePF("12345678900"));
        assertEquals("CPF inválido.", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoCPFForNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> criarClientePF(null));
        assertEquals("CPF inválido.", exception.getMessage());
    }

    @Test
    void deveCriarClienteComCNPJModelo1Valido() {
        val cliente = criarClientePJ("19131243000197");
        assertEquals("Nome Empresa Teste", cliente.getNome());
        assertEquals("19131243000197", cliente.getDocumento());
        assertTrue(cliente.getAtivo());
    }

    @Test
    void deveCriarClienteComCNPJModelo2Valido() {
        val cliente = criarClientePJ("12ABC34501DE35");
        assertEquals("Nome Empresa Teste", cliente.getNome());
        assertEquals("12ABC34501DE35", cliente.getDocumento());
        assertTrue(cliente.getAtivo());
    }

    @Test
    void deveLancarExcecaoQuandoCNPJForInvalido() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> criarClientePJ("19131243000198"));
        assertEquals("CNPJ inválido.", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoCNPJForInvalido2() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> criarClientePJ("12ABC345"));
        assertEquals("CNPJ inválido.", exception.getMessage());
    }

    private static @NonNull Cliente criarClientePF(String documento) {
        return new Cliente("F",
                documento,
                "João Silva",
                null,
                null,
                "joao@gmail.com",
                "47988774455");
    }

    private static @NonNull Cliente criarClientePJ(String documento) {
        return new Cliente("J",
                documento,
                "Nome Empresa Teste",
                "12345678912345678925",
                "Empresa Teste Fantasia",
                "empresa.teste@email.com",
                "47987654322");
    }


    @Test
    void deveAtualizarContatoSemAlterarDocumentoTipoDataOuAtivacao() {
        var cliente = criarClientePF("52998224725");
        cliente.setId(1L);
        cliente.desativar();
        var dataCriacao = cliente.getDataCriacao();

        cliente.atualizarDados("Novo Nome", "123", "Fantasia", "novo@email.com", "47999999999");

        assertEquals("Novo Nome", cliente.getNome());
        assertEquals("123", cliente.getInscricaoEstadual());
        assertEquals("Fantasia", cliente.getNomeFantasia());
        assertEquals("novo@email.com", cliente.getEmail());
        assertEquals("47999999999", cliente.getTelefone());
        assertEquals(1L, cliente.getId());
        assertEquals("52998224725", cliente.getDocumento());
        assertEquals("F", cliente.getTipoPessoa());
        assertEquals(dataCriacao, cliente.getDataCriacao());
        assertFalse(cliente.getAtivo());
    }

    @Test
    void deveSubstituirEnderecosSemAcumularOsAnteriores() {
        var cliente = criarClientePF("52998224725");
        cliente.adicionarEndereco(endereco("Rua Antiga"));
        var novo = endereco("Rua Nova");
        var enderecos = new ArrayList<>(List.of(novo));

        cliente.substituirEnderecos(enderecos);
        enderecos.clear();

        assertEquals(List.of(novo), cliente.getEnderecos());
    }

    @Test
    void deveLimparEnderecosAoSubstituirPorNull() {
        var cliente = criarClientePF("52998224725");
        cliente.adicionarEndereco(endereco("Rua A"));

        cliente.substituirEnderecos(null);

        assertTrue(cliente.getEnderecos().isEmpty());
    }

    @Test
    void deveIgnorarEnderecoNuloSemRemoverExistentes() {
        var cliente = criarClientePF("52998224725");
        var endereco = endereco("Rua A");
        cliente.adicionarEndereco(endereco);

        cliente.adicionarEndereco(null);

        assertEquals(List.of(endereco), cliente.getEnderecos());
    }

    @Test
    void devePermitirDesativacaoRepetidaSemAlterarIdentidade() {
        var cliente = criarClientePF("52998224725");

        cliente.desativar();
        cliente.desativar();

        assertFalse(cliente.getAtivo());
        assertEquals("52998224725", cliente.getDocumento());
        assertEquals("João Silva", cliente.getNome());
    }

    private Endereco endereco(String logradouro) {
        return new Endereco("89201000", logradouro, "10", null, "Centro", "Joinville", "SC");
    }
}