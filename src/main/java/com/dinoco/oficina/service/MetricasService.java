package com.dinoco.oficina.service;

import com.dinoco.oficina.dto.DetalheMetricaServicoDTO;
import com.dinoco.oficina.dto.RelatorioMetricasDTO;
import com.dinoco.oficina.repository.ItemOSServicoRepository;
import com.dinoco.oficina.repository.projection.ServicoMetricaProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricasService {

    private final ItemOSServicoRepository repository;

    public RelatorioMetricasDTO gerarRelatorioTempoExecucao(LocalDate inicio, LocalDate fim) {
        var dataInicio = (inicio != null) ? inicio.atStartOfDay() : null;
        var dataFim = (fim != null) ? fim.atTime(LocalTime.MAX) : null;
        var projecoes = repository.consultarMetricas(dataInicio, dataFim);
        List<DetalheMetricaServicoDTO> detalhes = projecoes.stream()
                .map(this::calcularEConverterDetalhe)
                .toList();
        return new RelatorioMetricasDTO(LocalDateTime.now(), inicio, fim, detalhes);
    }

    private DetalheMetricaServicoDTO calcularEConverterDetalhe(ServicoMetricaProjection p) {
        double diff = p.getMediaMinutos() - p.getTempoPadrao();
        double percentual = p.getTempoPadrao() > 0 ? (diff / p.getTempoPadrao()) * 100 : 0.0;
        double desvioAbsoluto = p.getMediaMinutos() - p.getTempoPadrao();

        return new DetalheMetricaServicoDTO(
                p.getServicoId(),
                p.getDescricao(),
                p.getQuantidade(),
                p.getMediaMinutos(),
                p.getTempoPadrao(),
                desvioAbsoluto,
                percentual
        );
    }
}
