CREATE TABLE servico (
     id BIGSERIAL PRIMARY KEY,
     descricao VARCHAR(255) NOT NULL UNIQUE,
     preco_padrao NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
     tempo_estimado_minutos INTEGER NOT NULL DEFAULT 0,
     ativo BOOLEAN NOT NULL DEFAULT TRUE,
     data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_servico_descricao ON servico(descricao);

-- Inserindo serviços padrões
INSERT INTO servico (descricao, preco_padrao, tempo_estimado_minutos) VALUES
      ('Troca de óleo e filtro', 80.00, 30),
      ('Alinhamento de rodas', 120.00, 45),
      ('Troca de pastilhas de freio', 150.00, 60),
      ('Limpeza de bico injetor', 200.00, 90),
      ('Troca de correia dentada', 350.00, 180),
      ('Higienização de ar condicionado', 90.00, 40);