package com.dinoco.oficina.service;
/*
import com.dinoco.oficina.dto.DetalheMetricaServicoDTO;
import com.dinoco.oficina.dto.RelatorioMetricasDTO;
import com.dinoco.oficina.repository.ItemOSServicoRepository;
import com.dinoco.oficina.repository.projection.ServicoMetricaProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
        BigDecimal media = BigDecimal.valueOf(p.getMediaMinutos());
        BigDecimal padrao = BigDecimal.valueOf(p.getTempoPadraoMinutos());
        BigDecimal desvio = media.subtract(padrao).setScale(2, RoundingMode.HALF_UP);
        BigDecimal percentual = BigDecimal.ZERO;
        if (p.getTempoPadraoMinutos() > 0) {
            percentual = desvio.multiply(BigDecimal.valueOf(100))
                    .divide(padrao, 2, RoundingMode.HALF_UP);
        }

        return new DetalheMetricaServicoDTO(
                p.getServicoId(),
                p.getDescricao(),
                p.getQuantidade(),
                media.setScale(2, RoundingMode.HALF_UP),
                p.getTempoPadraoMinutos(),
                desvio,
                percentual
        );
    }
}

 */
