CREATE TABLE veiculo (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    placa VARCHAR(8) NOT NULL UNIQUE,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    ano_fabricacao INTEGER,
    ano_modelo INTEGER,
    cor VARCHAR(30),
    chassi VARCHAR(17),
    motor VARCHAR(50),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_veiculo_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

CREATE INDEX idx_veiculo_placa ON veiculo(placa);
CREATE INDEX idx_veiculo_cliente_id ON veiculo(cliente_id);