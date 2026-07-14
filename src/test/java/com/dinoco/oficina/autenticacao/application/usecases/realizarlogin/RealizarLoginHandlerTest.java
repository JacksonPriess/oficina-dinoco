package com.dinoco.oficina.autenticacao.application.usecases.realizarlogin;

import com.dinoco.oficina.autenticacao.application.gateways.AutenticacaoCommandGateway;
import com.dinoco.oficina.autenticacao.application.gateways.TokenGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RealizarLoginHandlerTest {

	@Mock
	private AutenticacaoCommandGateway autenticacaoCommandGateway;

	@Mock
	private TokenGateway tokenGateway;

	@Test
	@DisplayName("Deve retornar token quando credenciais válidas")
	void deveRetornarTokenQuandoAutenticado() {
		// Arrange
		String username = "usuario.teste";
		String password = "senha123";
		when(autenticacaoCommandGateway.autenticar(username, password)).thenReturn(true);
		when(tokenGateway.gerarToken(username)).thenReturn("token-abc-123");

		RealizarLoginHandler handler = new RealizarLoginHandler(autenticacaoCommandGateway, tokenGateway);

		// Act
		RealizarLoginOutput output = handler.executar(new RealizarLoginCommand(username, password));

		// Assert
		assertNotNull(output);
		assertEquals("token-abc-123", output.token());
		verify(autenticacaoCommandGateway, times(1)).autenticar(username, password);
		verify(tokenGateway, times(1)).gerarToken(username);
	}

	@Test
	@DisplayName("Deve lançar exceção quando credenciais inválidas")
	void deveLancarExcecaoQuandoNaoAutenticado() {
		// Arrange
		String username = "usuario.teste";
		String password = "senhaErrada";
		when(autenticacaoCommandGateway.autenticar(username, password)).thenReturn(false);

		RealizarLoginHandler handler = new RealizarLoginHandler(autenticacaoCommandGateway, tokenGateway);

		// Act & Assert
		RuntimeException ex = assertThrows(RuntimeException.class, () ->
				handler.executar(new RealizarLoginCommand(username, password))
		);
		assertEquals("Credenciais inválidas", ex.getMessage());

		verify(autenticacaoCommandGateway, times(1)).autenticar(username, password);
		verify(tokenGateway, never()).gerarToken(anyString());
	}

}