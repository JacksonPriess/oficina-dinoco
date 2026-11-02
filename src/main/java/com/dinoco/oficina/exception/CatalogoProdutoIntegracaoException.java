package com.dinoco.oficina.exception;

public class CatalogoProdutoIntegracaoException extends RuntimeException {

    public CatalogoProdutoIntegracaoException(String mensagem) {
        super(mensagem);
    }

    public CatalogoProdutoIntegracaoException(
            String mensagem,
            Throwable causa
    ) {
        super(mensagem, causa);
    }
}