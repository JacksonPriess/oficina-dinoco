package com.dinoco.oficina.cliente.infrastructure.persistence;

import com.dinoco.oficina.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;

class ClienteJpaRepositoryTest extends BaseIT {

    @Autowired
    private ClienteJpaRepository repository;

    @Test
    void deveSalvarClientePFNoBanco() {
        // Arrange
        var entity = new ClienteEntity();
        entity.setTipoPessoa("F");
        entity.setDocumento("12345678901");
        entity.setNome("João da Silva");
        entity.setInscricaoEstadual(null);
        entity.setNomeFantasia(null);
        entity.setEmail("joao@email.com");
        entity.setTelefone("47999999999");
        entity.setAtivo(true);

        var endereco = new EnderecoEntity();
        endereco.setLogradouro("Rua das Flores");
        endereco.setNumero("100");
        endereco.setBairro("Centro");
        endereco.setCidade("Porto Belo");
        endereco.setUf("SC");
        endereco.setCep("88210000");
        entity.addEndereco(endereco);

        // Act
        var salvo = repository.save(entity);
        var encontrado = repository.findById(salvo.getId());

        // Assert
        assertTrue(encontrado.isPresent());
        var cliente = encontrado.get();
        assertNotNull(cliente.getId());
        assertEquals("F", cliente.getTipoPessoa());
        assertEquals("12345678901", cliente.getDocumento());
        assertEquals("João da Silva", cliente.getNome());
        assertNull(cliente.getInscricaoEstadual());
        assertNull(cliente.getNomeFantasia());
        assertEquals("joao@email.com", cliente.getEmail());
        assertEquals("47999999999", cliente.getTelefone());
        assertTrue(cliente.getAtivo());
        assertNotNull(cliente.getDataCriacao());
    }
}