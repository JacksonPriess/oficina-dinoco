package com.dinoco.oficina.exception;

import com.dinoco.oficina.ordemservico.domain.exceptions.RegraNegocioOSException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tools.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleValidationErrors() {
        var bindingResult = mock(BindingResult.class);
        var fieldError = new FieldError("request", "nome", "não pode ser vazio");
        var exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        var response = handler.handleValidationErrors(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).satisfies(body -> {
            assertThat(body.timestamp()).isNotNull();
            assertThat(body.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(body.erro()).isEqualTo("Erro de validação de campos");
            assertThat(body.mensagens()).containsExactly("nome: não pode ser vazio");
        });
    }

    @Test
    void handleBusinessErrors() {
        var response = handler.handleBusinessErrors(new RegraNegocioOSException("Transição inválida"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().erro()).isEqualTo("Regra de negócio violada");
        assertThat(response.getBody().mensagens()).containsExactly("Transição inválida");
    }

    @Test
    void handleRecursoNaoEncontrado() {
        var response = handler.handleRecursoNaoEncontrado(new RecursoNaoEncontradoException("OS não encontrada"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().erro()).isEqualTo("Recurso não encontrado");
        assertThat(response.getBody().mensagens()).containsExactly("OS não encontrada");
    }

    @Test
    void handleGenericException() {
        var response = handler.handleGenericException(new Exception("detalhe interno"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().erro()).isEqualTo("Erro Interno no Servidor");
        assertThat(response.getBody().mensagens()).containsExactly(
                "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde ou contate o suporte.");
    }

    @Test
    void handleNoResourceFound() {
        var exception = mock(NoResourceFoundException.class);
        when(exception.getResourcePath()).thenReturn("/api/inexistente");

        var response = handler.handleNoResourceFound(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().erro()).isEqualTo("Rota não encontrada");
        assertThat(response.getBody().mensagens()).containsExactly(
                "O endpoint '/api/inexistente' não existe nesta API.");
    }

    @Test
    void handleBadCredentials() {
        var response = handler.handleBadCredentials(new BadCredentialsException("credenciais incorretas"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().erro()).isEqualTo("Falha na autenticação");
        assertThat(response.getBody().mensagens()).containsExactly("Usuário ou senha inválidos.");
    }

    @Test
    void handleHttpMessageNotReadable() {
        var exception = mock(HttpMessageNotReadableException.class);
        when(exception.getCause()).thenReturn(new IllegalArgumentException("corpo inválido"));

        var response = handler.handleHttpMessageNotReadable(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().erro()).isEqualTo("Erro de Requisição");
        assertThat(response.getBody().mensagens()).containsExactly(
                "Corpo da requisição mal formatado ou com tipos de dados inválidos.");
    }

    @Test
    void handleCatalogoProdutoIntegracao() {
        var response = handler.handleCatalogoProdutoIntegracao(
                new CatalogoProdutoIntegracaoException("falha do catálogo"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody().erro()).isEqualTo("Falha na integração com o Catálogo de Produtos");
        assertThat(response.getBody().mensagens()).containsExactly(
                "Não foi possível consultar os dados do produto. Tente novamente mais tarde.");
    }

    @Test
    void handleCatalogoServicoIntegracao() {
        var response = handler.handleCatalogoServicoIntegracao(
                new CatalogoServicoIntegracaoException("falha do catálogo"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody().erro()).isEqualTo("Falha na integração com o Catálogo de Serviços");
        assertThat(response.getBody().mensagens()).containsExactly(
                "Não foi possível consultar o catálogo de serviços.");
    }

}
