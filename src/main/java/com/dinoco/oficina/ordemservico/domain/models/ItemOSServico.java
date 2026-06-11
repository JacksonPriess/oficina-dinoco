package com.dinoco.oficina.ordemservico.domain.models;

import com.dinoco.oficina.ordemservico.domain.enums.StatusItemServico;
import com.dinoco.oficina.ordemservico.domain.exceptions.RegraNegocioOSException;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ItemOSServico {
    private Long id;
    private Long servicoId;
    private Long mecanicoId; // Pode ser nulo no início e atribuído depois
    private BigDecimal valorCobrado;
    private StatusItemServico statusItem;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    public ItemOSServico(Long servicoId, Long mecanicoId, BigDecimal valorCobrado) {
        if (valorCobrado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor cobrado não pode ser negativo.");
        }
        this.servicoId = servicoId;
        this.mecanicoId = mecanicoId;
        this.valorCobrado = valorCobrado;
        this.statusItem = StatusItemServico.PENDENTE;
    }

    public ItemOSServico(Long id, Long servicoId, Long mecanicoId, BigDecimal valorCobrado, StatusItemServico statusItem, LocalDateTime dataInicio, LocalDateTime dataFim) {
        this.id = id;
        this.servicoId = servicoId;
        this.mecanicoId = mecanicoId;
        this.valorCobrado = valorCobrado;
        this.statusItem = statusItem;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public void alterarDados(BigDecimal novoValorCobrado, Long novoMecanicoId) {
        if (this.statusItem == StatusItemServico.CONCLUIDO) {
            throw new RegraNegocioOSException("Não é possível alterar valores ou mecânico de um serviço já concluído.");
        }

        if (novoValorCobrado != null) {
            if (novoValorCobrado.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("O valor cobrado não pode ser negativo.");
            }
            this.valorCobrado = novoValorCobrado;
        }

        if (novoMecanicoId != null) {
            this.mecanicoId = novoMecanicoId;
        }
    }

    public void atribuirMecanico(Long mecanicoId) {
        if (this.statusItem == StatusItemServico.CONCLUIDO) {
            throw new IllegalStateException("Não é possível trocar o mecânico de um serviço já concluído.");
        }
        this.mecanicoId = mecanicoId;
    }

    public void iniciarExecucao() {
        if (this.statusItem != StatusItemServico.PENDENTE) {
            throw new IllegalStateException("Só é possível iniciar um serviço pendente.");
        }
        this.statusItem = StatusItemServico.EM_ANDAMENTO;
        this.dataInicio = LocalDateTime.now();
    }

    public void concluirExecucao(LocalDateTime dataFimManual) {
        if (this.statusItem != StatusItemServico.EM_ANDAMENTO) {
            throw new IllegalStateException("Só é possível concluir um serviço em andamento.");
        }
        this.statusItem = StatusItemServico.CONCLUIDO;
        this.dataFim = dataFimManual != null ? dataFimManual : LocalDateTime.now();
    }

}
