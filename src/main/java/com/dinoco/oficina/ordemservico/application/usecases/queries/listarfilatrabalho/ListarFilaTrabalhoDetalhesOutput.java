package com.dinoco.oficina.ordemservico.application.usecases.queries.listarfilatrabalho;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Projeção de leitura (DTO de saída do UseCase).
 * Contém apenas os dados necessários para exibir na tabela da tela de "Fila de Trabalho",
 * evitando carregar listas pesadas de peças e serviços desnecessariamente.
 */
public record ListarFilaTrabalhoDetalhesOutput(
        Long id,
        String codigoRastreio,
        Long clienteId, // Pode ser substituído pelo nomeCliente se o Gateway fizer o JOIN
        Long veiculoId, // Pode ser substituído pela placaVeiculo se o Gateway fizer o JOIN
        String status,
        LocalDateTime dataEntrada,
        BigDecimal valorTotalOS
) {}