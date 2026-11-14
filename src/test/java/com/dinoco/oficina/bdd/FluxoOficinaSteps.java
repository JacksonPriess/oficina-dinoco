package com.dinoco.oficina.bdd;

import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.cucumber.java.pt.*;
import io.restassured.response.Response;

import java.math.BigDecimal;
import java.sql.DriverManager;
import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * O Cucumber instancia esta classe a cada cenário. Não há estado estático compartilhado.
 */
public class FluxoOficinaSteps {

    private final BddApi api = new BddApi();
    private long clienteId, veiculoId, produtoId, servicoId, osId;
    private String sagaId, pagamentoId, provedorId;

    private String osPath() {
        return "/api/ordens-servico/" + osId;
    }

    private Response acaoNucleo(String method, String path, Object body) {
        return api.chamar("NUCLEO",
                          method,
                          path,
                          body);
    }

    private void acaoPostNucleo(String nome) {
        acaoNucleo("POST", osPath() + "/" + nome, null);
    }

    private void aguardar(String descricao, Runnable verifica) {
        await().alias(descricao).atMost(Duration.ofSeconds(90)).pollInterval(Duration.ofSeconds(1)).untilAsserted(
                verifica::run);
    }

    private void valor(Response response, String campo, String esperado) {
        assertThat(new BigDecimal(response.jsonPath().getString(campo))).as(campo).isEqualByComparingTo(esperado);
    }

    @Dado("que um funcionário está autenticado na oficina")
    public void autenticar() {
        api.validarConfiguracao();
        api.token = acaoNucleo(
                "POST",
                "/api/auth/login",
                Map.of("username", BddApi.env("BDD_LOGIN"), "password", BddApi.env("BDD_PASSWORD")))
                .jsonPath().getString("token");
        assertThat(api.token).isNotBlank();
    }

    @Dado("existe um cliente com veículo cadastrado")
    public void cadastrarClienteVeiculo() {
        clienteId = acaoNucleo(
                "POST",
                "/api/clientes",
                Map.of(
                        "tipoPessoa", "F",
                        "documento", gerarCPF(),
                        "nome", "Cliente BDD " + api.correlationId,
                        "email", "bdd@example.com",
                        "telefone", "47999999999",
                        "enderecos", List.of()))
                .jsonPath()
                .getLong("id");

        veiculoId = acaoNucleo(
                "POST",
                "/api/veiculos",
                Map.of(
                        "placa", "ABC1234",
                        "marca", "Fiat",
                        "modelo", "Uno",
                        "anoFabricacao", 2020,
                        "anoModelo", 2021,
                        "cor", "Branco"))
                .jsonPath()
                .getLong("id");
    }

    @Dado("existe um produto de 50 reais com 10 unidades em estoque")
    public void cadastrarProduto() {
        produtoId = api.chamar("PRODUTO",
                               "POST",
                               "/api/produtos",
                               Map.of("nome", "Peça BDD " + api.correlationId,
                                      "tipo", "PECA",
                                      "quantidade", 10,
                                      "precoCusto", 30,
                                      "precoVenda", 50)).jsonPath().getLong("id");

        aguardar("Saldo criado pelo evento do catálogo", () -> estoque("10", "0"));
    }

    @Dado("existe um serviço de 150 reais no catálogo")
    public void cadastrarServico() {
        servicoId = api.chamar("SERVICO",
                               "POST",
                               "/api/servicos",
                               Map.of("descricao", "Serviço BDD " + api.correlationId,
                                      "precoPadrao", 150,
                                      "tempoEstimadoMinutos", 30)).jsonPath().getLong("id");
    }

    @Quando("abro uma OS com 2 unidades do produto e o serviço")
    public void abrir() {
        osId = acaoNucleo("POST",
                          "/api/ordens-servico",
                          Map.of("clienteId",
                                 clienteId,
                                 "veiculoId", veiculoId,
                                 "quilometragemEntrada", 50000,
                                 "reclamacaoCliente", "Manutenção BDD " + api.correlationId,
                                 "produtos", List.of(Map.of("produtoId", produtoId,
                                                            "quantidade", 2)),
                                 "servicos", List.of(Map.of("servicoId", servicoId))))
                .jsonPath().getLong("id");
    }

    @Então("o orçamento deve totalizar 250 reais")
    public void orcamento() {
        var os = acaoNucleo("GET", osPath(), null);
        valor(os, "valorTotalProdutos", "100");
        valor(os, "valorTotalServicos", "150");
        valor(os, "valorTotalOS", "250");
    }

    @Quando("inicio e diagnóstico")
    public void iniciarDiagnostico() {
        acaoPostNucleo("iniciar-diagnostico");
    }

    @Quando("concluo o diagnóstico")
    public void concluirDiagnostico() {
        acaoNucleo("POST",
                   osPath() + "/concluir-diagnostico",
                   Map.of("laudo", "Substituir peça e executar serviço"));
    }

    @Quando("envio e aprovo o orçamento")
    public void aprovarOrcamento() {
        acaoPostNucleo("enviar-orcamento");
        acaoPostNucleo("aprovar");
        assertThat(acaoNucleo("GET",
                              osPath(),
                              null).jsonPath().getString("status")).isEqualTo("AGUARDANDO_EXECUCAO");
    }

    @Então("devem estar reservadas 2 unidades do produto")
    public void reserva() {
        estoque("10", "2");
    }

    @Quando("executo o serviço e finalizo a OS")
    public void iniciarEFinalizarExecucaoOSEItensServicos() {
        acaoPostNucleo("iniciar-execucao");
        long itemId = acaoNucleo(
                "GET",
                osPath(),
                null).jsonPath().getLong("itensServico[0].id");

        acaoNucleo("POST",
                   osPath() + "/servicos/" + itemId + "/iniciar-execucao",
                   null);

        acaoNucleo("POST",
                   osPath() + "/servicos/" + itemId + "/concluir-execucao",
                   Map.of());

        acaoPostNucleo("finalizar-execucao");

        assertThat(acaoNucleo("GET", osPath(), null).jsonPath().getString("status")).isEqualTo("FINALIZADA");
    }

    @Então("o estoque deve conter 8 unidades reais e nenhuma reservada")
    public void baixa() {
        estoque("8", "0");
    }

    private void estoque(String real, String reservada) {
        var response = acaoNucleo("GET", "/api/produtos/" + produtoId, null);
        assertThat(response.jsonPath().getLong("produtoId")).isEqualTo(produtoId);
        valor(response, "quantidadeReal", real);
        valor(response, "quantidadeReservada", reservada);
    }

    @Então("deve existir um pagamento pendente de 250 reais para essa OS")
    public void pagamento() {
        aguardar("Pagamento criado no provedor e reconhecido pela Saga", () -> {
            var saga = acaoNucleo(
                    "GET",
                    osPath() + "/pagamento",
                    null).jsonPath();
            assertThat(saga.getString("estado")).isEqualTo("PENDENTE");

            sagaId = saga.getString("sagaId");
            var response = api.chamar(
                    "FINANCEIRO",
                    "GET",
                    "/api/pagamentos/" + sagaId,
                    null);
            var pagamento = response.jsonPath();
            assertThat(pagamento.getString("status")).isEqualTo("PENDENTE");
            assertThat(pagamento.getLong("osId")).isEqualTo(osId);
            assertThat(pagamento.getString("sagaId")).isEqualTo(sagaId);
            valor(response, "valor", "250");
            pagamentoId = pagamento.getString("pagamentoId");
            provedorId = pagamento.getString("provedorId");
            assertThat(pagamentoId).isNotBlank();
            assertThat(provedorId).isNotBlank();
            assertThat(saga.getString("pagamentoId")).isEqualTo(pagamentoId);
        });
    }

    @Quando("aprovo esse pagamento no provedor mock")
    public void aprovarPagamento() {
        api.chamar(
                "MOCK",
                "POST",
                "/simulacao/pagamentos/" + provedorId + "/aprovar",
                null);
    }

    @Então("o núcleo deve reconhecer a aprovação na mesma Saga")
    public void aprovado() {
        aguardar("Aprovação recebida pelo núcleo", () -> conferirSaga("APROVADA"));
    }

    private void conferirSaga(String estado) {
        var saga = acaoNucleo(
                "GET",
                osPath() + "/pagamento",
                null).jsonPath();
        assertThat(saga.getString("sagaId")).isEqualTo(sagaId);
        assertThat(saga.getString("pagamentoId")).isEqualTo(pagamentoId);
        assertThat(saga.getString("estado")).isEqualTo(estado);
    }

    @Quando("concluo a ordem de serviço")
    public void concluir() {
        acaoPostNucleo("concluir");
    }

    @Então("a OS e sua Saga devem estar entregues")
    public void entregue() {
        assertThat(acaoNucleo(
                "GET",
                osPath(),
                null).jsonPath().getString("status")).isEqualTo("ENTREGUE");
        conferirSaga("ENTREGUE");
    }

    @After
    public void evidencias(Scenario scenario) {
        scenario.log("correlationId=" + api.correlationId + " clienteId=" + clienteId + " veiculoId=" + veiculoId
                             + " produtoId=" + produtoId + " servicoId=" + servicoId + " osId=" + osId + " sagaId=" + sagaId);
    }

    private String gerarCPF() {
        var random = new java.security.SecureRandom();
        var value = new StringBuilder();
        value.append(1 + random.nextInt(9));
        value.append(0); // evita sequência com todos os dígitos iguais
        for (int i = 2; i < 9; i++)
            value.append(random.nextInt(10));
        for (int length = 9; length <= 10; length++) {
            int sum = 0;
            for (int i = 0; i < length; i++) sum += (value.charAt(i) - '0') * (length + 1 - i);
            int digit = 11 - sum % 11;
            value.append(digit >= 10 ? 0 : digit);
        }
        return value.toString();
    }
}
