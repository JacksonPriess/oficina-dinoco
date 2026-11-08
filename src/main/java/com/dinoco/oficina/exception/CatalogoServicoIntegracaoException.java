package com.dinoco.oficina.exception;

public class CatalogoServicoIntegracaoException extends RuntimeException {

    public CatalogoServicoIntegracaoException(String mensagem) {
        super(mensagem);
    }

    public CatalogoServicoIntegracaoException(
            String mensagem,
            Throwable causa
    ) {
        super(mensagem, causa);
    }
}
