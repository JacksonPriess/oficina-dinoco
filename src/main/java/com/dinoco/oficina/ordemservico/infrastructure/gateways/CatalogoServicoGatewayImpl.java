package com.dinoco.oficina.ordemservico.infrastructure.gateways;

import com.dinoco.oficina.exception.CatalogoServicoIntegracaoException;
import com.dinoco.oficina.ordemservico.application.gateways.CatalogoServicoGateway;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CatalogoServicoGatewayImpl implements CatalogoServicoGateway {

    private final RestClient restClient;

    public CatalogoServicoGatewayImpl(RestClient.Builder builder,
            @Value("${integracoes.catalogo-servico.base-url}") String baseUrl,
            @Value("${integracoes.catalogo-servico.api-key}") String apiKey) {
        var httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
        var factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(5));
        this.restClient = builder.clone()
                .baseUrl(baseUrl)
                .defaultHeader("X-API-Key", apiKey)
                .requestFactory(factory)
                .build();
    }

    @Override
    public Optional<BigDecimal> buscarPrecoPadrao(Long servicoId) {
        try {
            var response = restClient.get().uri("/api/servicos/{id}", servicoId)
                    .retrieve().body(ServicoResponse.class);
            if (response == null || !servicoId.equals(response.id())
                    || response.precoPadrao() == null || response.precoPadrao().signum() < 0) {
                throw new CatalogoServicoIntegracaoException("Preço inválido para o serviço " + servicoId);
            }
            return Optional.of(response.precoPadrao());
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) return Optional.empty();
            throw falha(ex);
        } catch (RestClientException ex) {
            throw falha(ex);
        }
    }

    @Override
    public Map<Long, ServicoCatalogo> buscarDetalhes(List<Long> ids) {
        var distintos = ids.stream().distinct().toList();
        Map<Long, ServicoCatalogo> resultado = new LinkedHashMap<>();
        for (int i = 0; i < distintos.size(); i += 100) {
            var lote = distintos.subList(i, Math.min(i + 100, distintos.size()));
            var query = lote.stream().map(String::valueOf).collect(Collectors.joining(","));
            ServicoResponse[] response;
            try {
                response = restClient.get().uri(uri -> uri.path("/api/servicos").queryParam("ids", query).build())
                        .retrieve().body(ServicoResponse[].class);
            } catch (RestClientException ex) {
                throw falha(ex);
            }
            if (response == null) throw new CatalogoServicoIntegracaoException("Resposta em lote ausente.");
            for (var servico : response) {
                if (servico == null || servico.id() == null || !lote.contains(servico.id())
                        || servico.descricao() == null || servico.tempoEstimadoMinutos() == null) {
                    throw new CatalogoServicoIntegracaoException("Resposta inválida na consulta em lote.");
                }
                resultado.put(servico.id(), new ServicoCatalogo(servico.id(), servico.descricao(),
                        servico.precoPadrao(), servico.tempoEstimadoMinutos()));
            }
        }
        return resultado;
    }

    private CatalogoServicoIntegracaoException falha(RestClientException ex) {
        String status = ex instanceof RestClientResponseException resposta
                ? " HTTP " + resposta.getStatusCode().value() : "";
        return new CatalogoServicoIntegracaoException("Não foi possível consultar o Catálogo de Serviços." + status, ex);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ServicoResponse(Long id, String descricao, BigDecimal precoPadrao, Integer tempoEstimadoMinutos) {}
}
