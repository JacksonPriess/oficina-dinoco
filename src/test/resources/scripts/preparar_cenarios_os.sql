-- 1. Limpeza garantida (Idempotência, respeitando Foreign Keys de baixo para cima)
DELETE FROM saldo_estoque;
DELETE FROM movimentacao_estoque;
DELETE FROM item_os_produto;
DELETE FROM item_os_servico;
DELETE FROM ordem_servico;
DELETE FROM servico;
DELETE FROM veiculo;
DELETE FROM cliente;

-- 2. Cadastros Base Compartilhados
INSERT INTO cliente (id, tipo_pessoa, documento, nome, email, telefone)
VALUES (999, 'F', '64518540000', 'João da Silva', 'joao.silva@email.com', '47999990001');

INSERT INTO veiculo (id, placa, marca, modelo, ano_fabricacao, ano_modelo, cor, chassi, motor)
VALUES (888, 'BRA2E45', 'Volkswagen', 'Gol', '2018', '2019', 'Branco', '9BWZZZ', '1.6 MSI');

INSERT INTO servico (id, descricao, preco_padrao, tempo_estimado_minutos, ativo, data_criacao)
VALUES (666, 'Troca de Pastilha de Freio', 120.00, 60, true, current_timestamp);

-- Estoque local para o produto externo usado nos cenários da OS.
INSERT INTO saldo_estoque (
    produto_id,
    quantidade_real,
    quantidade_reservada,
    versao
)
VALUES (777, 10.000, 0.000, 0);
-- ====================================================================================
-- 3. CENÁRIOS DE ORDENS DE SERVIÇO PARA CADA TESTE
-- ====================================================================================

-- Cenário A (Para Iniciar Diagnóstico): Status apenas RECEBIDA
INSERT INTO ordem_servico (id, cliente_id, veiculo_id, quilometragem_entrada, reclamacao_cliente, status, codigo_rastreio, data_entrada)
VALUES (100, 999, 888, 50000, 'O veículo está com algum problema','RECEBIDA', 'OS-100', current_date);

-- Cenário B (Para Concluir Diagnóstico): Status EM_DIAGNOSTICO + Possui Item de Serviço (obrigatório)
INSERT INTO ordem_servico (id, cliente_id, veiculo_id, quilometragem_entrada, reclamacao_cliente, status, codigo_rastreio, data_inicio_diagnostico)
VALUES (101, 999, 888, 50000, 'O veículo está com algum problema', 'EM_DIAGNOSTICO', 'OS-101', current_date);
INSERT INTO item_os_servico (id, os_id, servico_id, valor_cobrado, status_item)
VALUES (1, 101, 666, 0.00, 'PENDENTE');

-- Cenário C (Para Enviar Orçamento): Status AGUARDANDO_ORCAMENTO + Itens com valores > 0 (obrigatório)
INSERT INTO ordem_servico (id, cliente_id, veiculo_id, quilometragem_entrada, reclamacao_cliente, status, codigo_rastreio, valor_total_os)
VALUES (102, 999, 888, 50000, 'Veiculo com problema','AGUARDANDO_ORCAMENTO', 'OS-102', 270.00);
INSERT INTO item_os_servico (id, os_id, servico_id, valor_cobrado, status_item)
VALUES (2, 102, 666, 120.00, 'PENDENTE');
INSERT INTO item_os_produto (id, os_id, produto_id, quantidade, valor_unitario_venda, valor_total)
VALUES (1, 102, 777, 1.000, 150.00, 150.00);

-- Cenário D (Para Aprovar/Reprovar Orçamento): Status AGUARDANDO_APROVACAO
INSERT INTO ordem_servico (id, cliente_id, veiculo_id, quilometragem_entrada, reclamacao_cliente, status, codigo_rastreio)
VALUES (103, 999, 888, 50000, 'Veiculo com problema', 'AGUARDANDO_APROVACAO', 'OS-103');
-- Também exige itens para a reserva de estoque do aprovar() funcionar
INSERT INTO item_os_produto (id, os_id, produto_id, quantidade, valor_unitario_venda, valor_total)
VALUES (2, 103, 777, 2.000, 150.00, 300.00);