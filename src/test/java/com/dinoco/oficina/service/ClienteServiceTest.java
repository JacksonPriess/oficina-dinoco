package com.dinoco.oficina.service;

import com.dinoco.oficina.dto.ClienteRequestDto;
import com.dinoco.oficina.dto.ClienteResponseDto;
import com.dinoco.oficina.entity.Cliente;
import com.dinoco.oficina.helper.ClienteRequestDtoHelper;
import com.dinoco.oficina.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    @Test
    void deveCriarClientePFComSucesso() {
        //Arrange
        var request = ClienteRequestDtoHelper.criarPessoaFisica("52998224725");

        Cliente clienteSalvo = new Cliente();
        clienteSalvo.setId(1L);
        clienteSalvo.setTipoPessoa("F");
        clienteSalvo.setDocumento("52998224725");
        clienteSalvo.setNome("João da Silva");

        when(repository.existsByDocumento(anyString())).thenReturn(false);
        when(repository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        //Act
        ClienteResponseDto response = service.criar(request);

        //Assert
        assertThat(response)
                .isNotNull()
                .extracting(ClienteResponseDto::id, ClienteResponseDto::nome)
                .containsExactly(1L, "João da Silva");

        //Alternativa mais explicita
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("João da Silva");

        verify(repository, times(1)).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoQuandoDocumentoJaEstiverCadastrado() {

        var request = ClienteRequestDtoHelper.criarPessoaFisica("52998224725");

        // Ensina o mock: Diga que o documento já existe!
        when(repository.existsByDocumento(anyString())).thenReturn(true);

        // Verifica se a exceção correta foi lançada
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.criar(request);
        });

        assertEquals("Cliente já cadastrado com este documento.", exception.getMessage());
        verify(repository, never()).save(any(Cliente.class)); // Garante que NÃO tentou salvar no banco
    }

    @Test
    void deveLancarExcecaoQuandoDocumentoCpfForInvalido() {
        var request = ClienteRequestDtoHelper.criarPessoaFisica("12345678900");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.criar(request);
        });

        assertEquals("CPF inválido.", exception.getMessage());
        verify(repository, never()).existsByDocumento(anyString()); // A validação cai antes mesmo de ir ao banco
    }

    @Test
    void deveLancarExcecaoQuandoDocumentoCnpjForInvalido() {

        ClienteRequestDto request = new ClienteRequestDto(
                "J", "66666666666666", null, "Empresa do João da Silva",null, "empjoao@email.com",
                "4799999999", Collections.emptyList()
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.criar(request);
        });

        assertEquals("CNPJ inválido.", exception.getMessage());
        verify(repository, never()).existsByDocumento(anyString());
    }
}