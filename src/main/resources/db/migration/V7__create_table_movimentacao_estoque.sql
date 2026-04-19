CREATE TABLE movimentacao_estoque (
    id BIGSERIAL PRIMARY KEY,
    produto_id BIGINT NOT NULL REFERENCES produto(id),
    tipo_movimentacao VARCHAR(50) NOT NULL,
    quantidade DECIMAL(10,3) NOT NULL,
    data_movimentacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacao VARCHAR(255)
);

INSERT INTO movimentacao_estoque (produto_id, tipo_movimentacao, quantidade, data_movimentacao, observacao) VALUES
    (1, 'ENTRADA_FORNECEDOR', 50.000, CURRENT_TIMESTAMP, 'Movimentação inicial sistema'),
    (2, 'ENTRADA_FORNECEDOR', 12.000, CURRENT_TIMESTAMP, 'Movimentação inicial sistema'),
    (3, 'ENTRADA_FORNECEDOR', 2.000, CURRENT_TIMESTAMP, 'Movimentação inicial sistema'),
    (4, 'ENTRADA_FORNECEDOR', 15.000, CURRENT_TIMESTAMP, 'Movimentação inicial sistema'),
    (5, 'ENTRADA_FORNECEDOR', 10.000, CURRENT_TIMESTAMP, 'Movimentação inicial sistema'),
    (6, 'ENTRADA_FORNECEDOR', 24.000, CURRENT_TIMESTAMP, 'Movimentação inicial sistema'),
    (7, 'ENTRADA_FORNECEDOR', 20.000, CURRENT_TIMESTAMP, 'Movimentação inicial sistema'),
    (8, 'ENTRADA_FORNECEDOR', 5.000, CURRENT_TIMESTAMP, 'Movimentação inicial sistema'),
    (9, 'ENTRADA_FORNECEDOR', 30.000, CURRENT_TIMESTAMP, 'Movimentação inicial sistema'),
    (10, 'ENTRADA_FORNECEDOR', 12.000, CURRENT_TIMESTAMP, 'Movimentação inicial sistema');