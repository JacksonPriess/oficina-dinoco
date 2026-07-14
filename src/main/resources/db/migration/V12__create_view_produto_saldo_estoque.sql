CREATE VIEW view_produto_estoque AS
SELECT
    p.id,
    p.version,
    p.nome,
    p.tipo,
    p.marca,
    p.codigo_fabricante,
    p.aplicacao,
    p.preco_custo,
    p.preco_venda,
    p.ativo,
    COALESCE(s.quantidade_real, 0.000) AS quantidade_real,
    COALESCE(s.quantidade_reservada, 0.000) AS quantidade_reservada
FROM produto p
LEFT JOIN saldo_estoque s ON p.id = s.produto_id;