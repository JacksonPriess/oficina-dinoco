CREATE TABLE ordem_servico (
    id BIGSERIAL PRIMARY KEY,
    codigo_rastreio VARCHAR(50) UNIQUE NOT NULL,
    cliente_id BIGINT NOT NULL REFERENCES cliente(id),
    veiculo_id BIGINT NOT NULL REFERENCES veiculo(id),
    status VARCHAR(50) NOT NULL,
    quilometragem_entrada INTEGER NOT NULL,
    reclamacao_cliente TEXT NOT NULL,
    laudo_tecnico TEXT,
    valor_total_servicos DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    valor_total_produtos DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    valor_desconto DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    valor_total_os DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    data_entrada TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_saida TIMESTAMP,
    data_reprovacao TIMESTAMP
);