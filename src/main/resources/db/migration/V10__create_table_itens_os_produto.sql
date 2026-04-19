CREATE TABLE item_os_produto (
    id BIGSERIAL PRIMARY KEY,
    os_id BIGINT NOT NULL REFERENCES ordem_servico(id) ON DELETE CASCADE,
    produto_id BIGINT NOT NULL REFERENCES produto(id),
    quantidade DECIMAL(10,3) NOT NULL,
    valor_unitario_venda DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    valor_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    foi_encomendado BOOLEAN NOT NULL DEFAULT FALSE
);