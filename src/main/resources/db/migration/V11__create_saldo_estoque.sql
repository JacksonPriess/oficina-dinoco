CREATE TABLE saldo_estoque (
    id BIGSERIAL PRIMARY KEY,
    produto_id BIGINT NOT NULL UNIQUE REFERENCES produto(id),
    quantidade_real DECIMAL(10,3) NOT NULL DEFAULT 0.000,
    quantidade_reservada DECIMAL(10,3) NOT NULL DEFAULT 0.000,
    versao BIGINT NOT NULL DEFAULT 0
);