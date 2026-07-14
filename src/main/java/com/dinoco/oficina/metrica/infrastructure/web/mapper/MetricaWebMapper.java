package com.dinoco.oficina.metrica.infrastructure.web.mapper;

import com.dinoco.oficina.metrica.application.usecase.queries.buscarmediaexecucao.BuscarMediaExecucaoServicosOutput;
import com.dinoco.oficina.metrica.application.usecase.queries.buscarmediaexecucao.BuscarMediaExecucaoServicosQuery;
import com.dinoco.oficina.metrica.infrastructure.web.dto.RelatorioMetricasDTO;
import com.dinoco.oficina.metrica.infrastructure.web.dto.RelatorioMetricasRequestDTO;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MetricaWebMapper {

    BuscarMediaExecucaoServicosQuery toInput(@Valid RelatorioMetricasRequestDTO request);

    RelatorioMetricasDTO toRelatorioMetricasDTO(BuscarMediaExecucaoServicosOutput buscarMediaExecucaoServicosOutput);
}
