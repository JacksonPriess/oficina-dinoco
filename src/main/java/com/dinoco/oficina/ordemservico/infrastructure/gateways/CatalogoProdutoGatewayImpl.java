package com.dinoco.oficina.ordemservico.infrastructure.gateways;

import com.dinoco.oficina.exception.CatalogoProdutoIntegracaoException;
import com.dinoco.oficina.ordemservico.application.gateways.CatalogoProdutoGateway;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.Duration;

@Component
public class CatalogoProdutoGatewayImpl implements CatalogoProdutoGateway {

    private final RestClient restClient;

    public CatalogoProdutoGatewayImpl( RestClient.Builder builder,
            @Value("${integracoes.catalogo-produto.base-url}") String baseUrl,
            @Value("${integracoes.catalogo-produto.api-key}") String apiKey
    ) {
        var httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();

        var requestFactory =
                new JdkClientHttpRequestFactory(httpClient);

        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        this.restClient = builder
                .baseUrl(baseUrl)
                .defaultHeader("X-API-Key", apiKey)
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public BigDecimal buscarPrecoVendaAtual(Long produtoId) {
        ProdutoResponse response;

        try {
            response = restClient.get()
                    .uri("/api/produtos/{id}", produtoId)
                    .retrieve()
                    .body(ProdutoResponse.class);

        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                return null;
            }
            throw new CatalogoProdutoIntegracaoException("Falha ao consultar o Catálogo de Produtos. HTTP " + ex.getStatusCode().value(), ex);

        } catch (RestClientException ex) {
            throw new CatalogoProdutoIntegracaoException("Não foi possível consultar o Catálogo de Produtos.", ex);
        }

        if (response == null
                || !produtoId.equals(response.id())
                || response.precoVenda() == null
                || response.precoVenda().signum() < 0) {

            throw new CatalogoProdutoIntegracaoException("Resposta inválida do Catálogo para o produto " + produtoId);
        }

        return response.precoVenda();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ProdutoResponse(
            Long id,
            BigDecimal precoVenda
    ) {
    }
}