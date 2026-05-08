package com.dinoco.oficina.repository.projection;

public interface ServicoMetricaProjection {
    Long getServicoId();
    String getDescricao();
    Long getQuantidade();
    Double getMediaMinutos();
    Integer getTempoPadraoMinutos();
}