package com.dinoco.oficina.metrica.application.usecase.queries.buscarmediaexecucao;

import com.dinoco.oficina.metrica.application.gateways.MetricaQueryGateway;
import com.dinoco.oficina.metrica.application.gateways.MetricaServicoData;
import com.dinoco.oficina.metrica.domain.MetricaServico;

import java.time.LocalDateTime;
import java.util.List;

public class BuscarMediaExecucaoServicosHandler implements BuscarMediaExecucaoServicosUseCase {

    private final MetricaQueryGateway metricaQueryGateway;

    public BuscarMediaExecucaoServicosHandler(MetricaQueryGateway metricaQueryGateway) {
        this.metricaQueryGateway = metricaQueryGateway;
    }

    @Override
    public BuscarMediaExecucaoServicosOutput executar(BuscarMediaExecucaoServicosQuery query) {

        List<MetricaServicoData> dadosBrutos = metricaQueryGateway.buscarDadosMetricas(query.inicio(), query.fim());

        List<BuscarMediaExecucaoServicosDetalhesOutput> detalhes = dadosBrutos.stream()
                .map(dado -> {
                    // O Domínio assume a responsabilidade dos cálculos
                    var dominio = new MetricaServico(
                            dado.servicoId(),
                            dado.descricao(),
                            dado.quantidade(),
                            dado.mediaMinutos(),
                            dado.tempoPadraoMinutos()
                    );

                    return new BuscarMediaExecucaoServicosDetalhesOutput(
                            dominio.servicoId(),
                            dominio.descricao(),
                            dominio.quantidade(),
                            dominio.mediaMinutos(),
                            dominio.tempoPadraoMinutos(),
                            dominio.calcularDesvioAbsoluto(),
                            dominio.calcularPercentualDiferenca()
                    );
                })
                .toList();

        // 3. Monta o envelope de saída
        var mediaExecucaoServicosOutput = new BuscarMediaExecucaoServicosOutput(
                LocalDateTime.now(),
                query.inicio(),
                query.fim(),
                detalhes
        );

        return mediaExecucaoServicosOutput;
    }
}
