CREATE TABLE produto (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    marca VARCHAR(100),
    codigo_fabricante VARCHAR(100),
    aplicacao TEXT,
    quantidade_atual DECIMAL(10,3) NOT NULL DEFAULT 0.000,
    quantidade_reservada DECIMAL(10,3) NOT NULL DEFAULT 0.000,
    preco_custo DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    preco_venda DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_reserva_valida CHECK (quantidade_atual >= quantidade_reservada),
    CONSTRAINT chk_quantidades_positivas CHECK (quantidade_atual >= 0 AND quantidade_reservada >= 0)
);

CREATE INDEX idx_produto_busca ON produto (nome, marca, codigo_fabricante);

INSERT INTO produto (nome, tipo, marca, codigo_fabricante, aplicacao, quantidade_atual, quantidade_reservada, preco_custo, preco_venda, ativo, version) VALUES
    ('Óleo de Motor 5W30 Sintético', 'INSUMO', 'Motorcraft', 'BXO5W30', 'Universal / Motores Sigma e Duratec', 50.000, 0.000, 35.00, 55.00, TRUE, 0),
    ('Filtro de Óleo', 'PECA', 'Fram', 'PH10060', 'Ford Focus 2009 a 2013', 12.000, 0.000, 25.00, 45.00, TRUE, 0),
    ('Radiador de Água', 'PECA', 'Visconde', '12543', 'Ford Focus 2009 a 2013 - Motor Sigma 1.6 e Duratec 2.0', 2.000, 0.000, 250.00, 450.00, TRUE, 0),
    ('Fluido de Freio DOT 4 500ml', 'INSUMO', 'Varga', 'DOT4-500', 'Universal', 15.000, 0.000, 18.00, 32.00, TRUE, 0),
    ('Filtro de Ar Condicionado', 'PECA', 'Tecfil', 'ACP903', 'Ford Focus 2009 a 2013', 10.000, 0.000, 22.00, 48.00, TRUE, 0),
    ('Vela de Ignição Iridium', 'PECA', 'NGK', 'TR6B-13', 'Motores Ford Zetec Rocam / Sigma', 24.000, 0.000, 45.00, 75.00, TRUE, 0),
    ('Aditivo para Radiador (Rosa)', 'INSUMO', 'Paraflu', '10-3054', 'Universal', 20.000, 0.000, 15.00, 28.00, TRUE, 0),
    ('Correia Dentada', 'PECA', 'Continental', 'CT1074', 'Motores Sigma 1.6 16v', 5.000, 0.000, 120.00, 210.00, TRUE, 0),
    ('Lâmpada H7 Farol Baixo', 'PECA', 'Osram', '64210', 'Universal 12v 55w', 30.000, 0.000, 12.00, 25.00, TRUE, 0),
    ('Descarbonizante Spray (Limpa TBI)', 'INSUMO', 'Car80', 'C80', 'Universal', 12.000, 0.000, 14.00, 35.00, TRUE, 0);