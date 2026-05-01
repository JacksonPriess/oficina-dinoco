CREATE TABLE movimentacao_estoque (
    id BIGSERIAL PRIMARY KEY,
    produto_id BIGINT NOT NULL REFERENCES produto(id),
    tipo_movimentacao VARCHAR(50) NOT NULL,
    quantidade DECIMAL(10,3) NOT NULL,
    data_movimentacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacao VARCHAR(255)
);