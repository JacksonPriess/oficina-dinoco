ALTER TABLE ordem_servico
    ADD COLUMN data_inicio_diagnostico TIMESTAMP,
    ADD COLUMN data_final_diagnostico TIMESTAMP,
    ADD COLUMN data_inicio_execucao TIMESTAMP,
    ADD COLUMN data_final_execucao TIMESTAMP;