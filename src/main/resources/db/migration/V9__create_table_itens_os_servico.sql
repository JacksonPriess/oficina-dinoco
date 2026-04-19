CREATE TABLE item_os_servico (
     id BIGSERIAL PRIMARY KEY,
     os_id BIGINT NOT NULL REFERENCES ordem_servico(id) ON DELETE CASCADE,
     servico_id BIGINT NOT NULL REFERENCES servico(id),
     funcionario_id BIGINT REFERENCES funcionario(id),
     valor_cobrado DECIMAL(10,2) NOT NULL DEFAULT 0.00,
     status_item VARCHAR(50) NOT NULL,
     data_inicio TIMESTAMP,
     data_fim TIMESTAMP
);