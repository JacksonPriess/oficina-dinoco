package com.dinoco.oficina.metrica.application.gateways;

import java.time.LocalDate;
import java.util.List;

public interface MetricaQueryGateway {

    List<MetricaServicoData> buscarDadosMetricas(LocalDate inicio, LocalDate fim);
}