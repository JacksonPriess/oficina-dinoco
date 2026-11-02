CREATE TABLE estoque_inicializacao (
       event_id UUID PRIMARY KEY,
       produto_id BIGINT NOT NULL UNIQUE,
       quantidade NUMERIC NOT NULL CHECK (quantidade >= 0),
       processado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
