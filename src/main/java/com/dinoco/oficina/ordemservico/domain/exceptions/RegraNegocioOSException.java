package com.dinoco.oficina.ordemservico.domain.exceptions;

public class RegraNegocioOSException extends RuntimeException {
    public RegraNegocioOSException(String mensagem) {
        super(mensagem);
    }
}