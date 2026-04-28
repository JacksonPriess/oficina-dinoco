package com.dinoco.oficina.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Intercepta erros de Validação (@Valid do DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroPadraoDto> handleValidationErrors(MethodArgumentNotValidException ex) {

        // Pega todos os campos que deram erro e extrai apenas a mensagem que escrevemos no DTO
        List<String> erros = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();

        ErroPadraoDto erroPadrao = new ErroPadraoDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação de Campos",
                erros
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erroPadrao);
    }

    // 2. Intercepta erros de Regra de Negócio (ex: IllegalArgumentException e IllegalStateExceptiondo Service)
    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
    public ResponseEntity<ErroPadraoDto> handleBusinessErrors(RuntimeException ex) {

        ErroPadraoDto erroPadrao = new ErroPadraoDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Regra de Negócio Violada",
                List.of(ex.getMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erroPadrao);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroPadraoDto> recursoNaoEncontradoException(RecursoNaoEncontradoException ex) {

        ErroPadraoDto erroPadrao = new ErroPadraoDto(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Recurso não encontrado",
                List.of(ex.getMessage())
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erroPadrao);
    }
}
