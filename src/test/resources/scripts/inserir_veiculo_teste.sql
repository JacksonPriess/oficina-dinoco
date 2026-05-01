DELETE FROM movimentacao_estoque;
DELETE FROM item_os_produto;
DELETE FROM item_os_servico;
DELETE FROM ordem_servico;
DELETE FROM produto;
DELETE FROM servico;
DELETE FROM veiculo;
DELETE FROM cliente;

INSERT INTO veiculo (id, placa, marca, modelo, ano_fabricacao, ano_modelo, cor, chassi, motor, ativo)
VALUES (100, 'KLA2024', 'Toyota', 'Corolla', 2024, 2024, 'Preto', '9BRZZZ', '2.0', true);