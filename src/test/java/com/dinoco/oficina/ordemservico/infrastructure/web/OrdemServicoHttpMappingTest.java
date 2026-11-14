package com.dinoco.oficina.ordemservico.infrastructure.web;

import com.dinoco.oficina.cliente.application.gateways.ClienteQueryGateway;
import com.dinoco.oficina.cliente.application.usecases.queries.buscarporid.BuscarClientePorIdOutput;
import com.dinoco.oficina.ordemservico.application.gateways.*;
import com.dinoco.oficina.ordemservico.application.saga.OrquestradorPagamento;
import com.dinoco.oficina.ordemservico.infrastructure.config.*;
import com.dinoco.oficina.ordemservico.infrastructure.gateways.*;
import com.dinoco.oficina.ordemservico.infrastructure.persistence.*;
import com.dinoco.oficina.ordemservico.infrastructure.web.mapper.OrdemServicoWebMapper;
import com.dinoco.oficina.exception.GlobalExceptionHandler;
import com.dinoco.oficina.shared.events.*;
import org.junit.jupiter.api.*;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import java.math.BigDecimal;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrdemServicoHttpMappingTest {
    private final OrdemServicoJpaRepository repository = mock(OrdemServicoJpaRepository.class);
    private final CatalogoProdutoGateway produtos = mock(CatalogoProdutoGateway.class);
    private final CatalogoServicoGateway servicos = mock(CatalogoServicoGateway.class);
    private final OrdemServicoEventPublisher eventos = mock(OrdemServicoEventPublisher.class);
    private final OrquestradorPagamento saga = mock(OrquestradorPagamento.class);
    private final VerificadorEstoqueGateway estoque = mock(VerificadorEstoqueGateway.class);
    private AnnotationConfigApplicationContext context;
    private MockMvc mvc;
    private OrdemServicoEntity persisted;

    @BeforeEach void configurar() {
        context = new AnnotationConfigApplicationContext();
        context.register(OrdemServicoConfig.class, ItemOSProdutoConfig.class, ItemOSServicoConfig.class,
                OrdemServicoCommandGatewayImpl.class, OrdemServicoQueryGatewayImpl.class,
                OrdemServicoSpringWebController.class, ItemOSProdutoSpringWebController.class, ItemOSServicoSpringWebController.class);
        context.registerBean(OrdemServicoJpaRepository.class, () -> repository);
        context.registerBean(CatalogoProdutoGateway.class, () -> produtos);
        context.registerBean(CatalogoServicoGateway.class, () -> servicos);
        context.registerBean(OrdemServicoEventPublisher.class, () -> eventos);
        context.registerBean(OrquestradorPagamento.class, () -> saga);
        context.registerBean(VerificadorEstoqueGateway.class, () -> estoque);
        context.registerBean(OrdemServicoWebMapper.class, () -> Mappers.getMapper(OrdemServicoWebMapper.class));
        var clientes = mock(ClienteQueryGateway.class);
        when(clientes.buscarDetalhesPorId(1L)).thenReturn(Optional.of(mock(BuscarClientePorIdOutput.class)));
        context.registerBean(ClienteQueryGateway.class, () -> clientes);
        var contato = mock(ClienteContatoGateway.class);
        when(contato.buscarContato(1L)).thenReturn(new ClienteContatoOutput("Cliente", "47999999999"));
        context.registerBean(ClienteContatoGateway.class, () -> contato);
        var funcionario = mock(FuncionarioGateway.class);
        when(funcionario.existeMecanicoAtivo(8L)).thenReturn(true);
        context.registerBean(FuncionarioGateway.class, () -> funcionario);
        context.registerBean(NamedParameterJdbcTemplate.class, () -> mock(NamedParameterJdbcTemplate.class));
        var tx = mock(PlatformTransactionManager.class);
        when(tx.getTransaction(any())).thenReturn(mock(TransactionStatus.class));
        context.registerBean(PlatformTransactionManager.class, () -> tx);
        context.refresh();
        mvc = MockMvcBuilders.standaloneSetup(context.getBean(OrdemServicoSpringWebController.class),
                context.getBean(ItemOSProdutoSpringWebController.class), context.getBean(ItemOSServicoSpringWebController.class))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        when(repository.save(any())).thenAnswer(i -> {
            persisted = i.getArgument(0); persisted.setId(10L);
            long id = 1;
            for (var item : persisted.getItensProduto()) item.setId(id++);
            id = 1;
            for (var item : persisted.getItensServico()) item.setId(id++);
            return persisted;
        });
        when(repository.findById(10L)).thenAnswer(i -> Optional.ofNullable(persisted));
        when(repository.bloquearPorId(10L)).thenAnswer(i -> Optional.ofNullable(persisted));
        when(repository.findByCodigoRastreio(anyString())).thenAnswer(i -> Optional.ofNullable(persisted));
        when(produtos.buscarPrecoVendaAtual(3L)).thenReturn(new BigDecimal("50"));
        when(servicos.buscarPrecoPadrao(4L)).thenReturn(Optional.of(new BigDecimal("150")));
        when(estoque.todasAsPecasEstaoDisponiveis(anyList())).thenReturn(true);
    }
    @AfterEach void fechar() { context.close(); }

    private void abrir() throws Exception {
        mvc.perform(post("/api/ordens-servico").contentType("application/json").content("""
                {"clienteId":1,"veiculoId":2,"quilometragemEntrada":1000,"reclamacaoCliente":"Ruído",
                 "produtos":[{"produtoId":3,"quantidade":2}],"servicos":[{"servicoId":4,"mecanicoId":8}]}
                """))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.valorTotalOS").value(250));
        assertThat(persisted.getItensProduto().getFirst().getOrdemServico()).isSameAs(persisted);
        assertThat(persisted.getItensServico().getFirst().getOrdemServico()).isSameAs(persisted);
        verify(produtos).buscarPrecoVendaAtual(3L);
        verify(servicos).buscarPrecoPadrao(4L);
    }
    private void acao(String path) throws Exception {
        mvc.perform(post("/api/ordens-servico/10/" + path)).andExpect(status().isNoContent());
    }
    private void diagnostico() throws Exception {
        acao("iniciar-diagnostico");
        mvc.perform(post("/api/ordens-servico/10/concluir-diagnostico").contentType("application/json").content("{\"laudo\":\"Trocar peça\"}"))
                .andExpect(status().isNoContent());
        mvc.perform(post("/api/ordens-servico/10/enviar-orcamento")).andExpect(status().isOk());
    }

    @Test void encaminharFluxoAteEntregaPreservandoItensValoresEEventos() throws Exception {
        abrir(); diagnostico(); acao("aprovar");
        verify(eventos).publicarOrcamentoAprovado(argThat(e -> e.osId().equals(10L) && e.itensOSProduto().size() == 1));
        acao("iniciar-execucao");
        verify(eventos).publicarExecucaoIniciada(any(ExecucaoIniciadaEvent.class));
        acao("servicos/1/iniciar-execucao");
        mvc.perform(post("/api/ordens-servico/10/servicos/1/concluir-execucao").contentType("application/json").content("{}"))
                .andExpect(status().isNoContent());
        acao("finalizar-execucao"); acao("concluir");
        verify(saga).iniciar(eq(10L), argThat(v -> v.compareTo(new BigDecimal("250")) == 0));
        verify(saga).autorizarEntrega(eq(10L), any());
        mvc.perform(get("/api/ordens-servico/10")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENTREGUE"))
                .andExpect(jsonPath("$.laudoTecnico").value("Trocar peça"))
                .andExpect(jsonPath("$.valorTotalOS").value(250));
        assertThat(persisted.getDataSaida()).isNotNull();
        assertThat(persisted.getItensServico().getFirst().getDataFim()).isNotNull();
    }
    @Test void adicionarEAlterarItensAtualizaOrcamento() throws Exception {
        abrir(); acao("iniciar-diagnostico");
        when(produtos.buscarPrecoVendaAtual(5L)).thenReturn(new BigDecimal("40"));
        when(servicos.buscarPrecoPadrao(6L)).thenReturn(Optional.of(new BigDecimal("90")));
        mvc.perform(post("/api/ordens-servico/10/produtos").contentType("application/json").content("{\"produtoId\":5,\"quantidade\":1}"))
                .andExpect(status().isNoContent());
        mvc.perform(post("/api/ordens-servico/10/servicos").contentType("application/json").content("{\"servicoId\":6}"))
                .andExpect(status().isNoContent());
        mvc.perform(put("/api/ordens-servico/10/produtos/1").contentType("application/json").content("{\"precoVenda\":60,\"quantidade\":3}"))
                .andExpect(status().isNoContent());
        mvc.perform(put("/api/ordens-servico/10/servicos/1").contentType("application/json").content("{\"valorCobrado\":180,\"mecanicoId\":8}"))
                .andExpect(status().isNoContent());
        assertThat(persisted.getItensServico().getFirst().getValorCobrado()).isEqualByComparingTo("180");
        assertThat(persisted.getItensProduto().getFirst().getQuantidade()).isEqualByComparingTo("3");
    }
    @Test void reprovacaoNaoPublicaReserva() throws Exception {
        abrir(); diagnostico(); acao("reprovar");
        mvc.perform(get("/api/ordens-servico/10")).andExpect(jsonPath("$.status").value("REPROVADA"));
        verifyNoInteractions(eventos);
    }
    @Test void rejeitarWebhookSemSegredoSemModificarOS() throws Exception {
        mvc.perform(post("/api/ordens-servico/webhooks/orcamentos/rastreio").contentType("application/json").content("{\"statusDecisao\":\"APROVADO\"}"))
                .andExpect(status().isUnauthorized());
        verify(repository, never()).save(any());
    }
    @Test void consultarOSAusente() throws Exception {
        mvc.perform(get("/api/ordens-servico/99")).andExpect(status().is4xxClientError());
        verify(repository).findById(99L);
    }
}
