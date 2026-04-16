CREATE TABLE cliente (
     id BIGSERIAL PRIMARY KEY,
     tipo_pessoa VARCHAR(1) NOT NULL,
     documento VARCHAR(14) NOT NULL UNIQUE,
     inscricao_estadual VARCHAR(20),
     nome VARCHAR(255) NOT NULL,
     nome_fantasia VARCHAR(255),
     email VARCHAR(255),
     telefone VARCHAR(20),
     ativo BOOLEAN NOT NULL DEFAULT TRUE,
     data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE endereco (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    cep VARCHAR(8),
    logradouro VARCHAR(255),
    numero VARCHAR(20),
    complemento VARCHAR(100),
    bairro VARCHAR(100),
    cidade VARCHAR(100),
    uf VARCHAR(2),

    CONSTRAINT fk_endereco_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE
);

CREATE INDEX idx_cliente_documento ON cliente(documento);
CREATE INDEX idx_endereco_cliente_id ON endereco(cliente_id);