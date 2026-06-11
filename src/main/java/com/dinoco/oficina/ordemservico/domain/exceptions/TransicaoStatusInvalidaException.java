package com.dinoco.oficina.ordemservico.domain.exceptions;

public class TransicaoStatusInvalidaException extends RuntimeException {
    public TransicaoStatusInvalidaException(String mensagem) {
        super(mensagem);
    }
}
