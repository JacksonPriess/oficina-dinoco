package com.dinoco.oficina.estoque.infrastructure;

import com.dinoco.oficina.estoque.infrastructure.config.EstoqueConfig;
import com.dinoco.oficina.estoque.infrastructure.persistence.*;
import com.dinoco.oficina.estoque.infrastructure.gateways.EstoqueQueryGatewayImpl;
import com.dinoco.oficina.estoque.infrastructure.web.*;
import com.dinoco.oficina.estoque.infrastructure.web.mapper.EstoqueWebMapper;
import com.dinoco.oficina.estoque.application.listeners.*;
import com.dinoco.oficina.estoque.domain.*;
import com.dinoco.oficina.ordemservico.domain.models.ItemOSProduto;
import com.dinoco.oficina.shared.events.*;
import com.dinoco.oficina.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.*;
import org.mapstruct.factory.Mappers;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.math.BigDecimal;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EstoqueFluxoTest {
    private final SaldoEstoqueJpaRepository saldos = mock(SaldoEstoqueJpaRepository.class);
    private final MovimentacaoEstoqueJpaRepository movimentos = mock(MovimentacaoEstoqueJpaRepository.class);
    private final EstoqueCommandGatewayImpl commands = new EstoqueCommandGatewayImpl(saldos, movimentos);
    private SaldoEstoqueEntity entity;
    private MockMvc mvc;

    @BeforeEach void configurar() {
        entity = new SaldoEstoqueEntity();
        entity.setId(1L); entity.setProdutoId(7L); entity.setQuantidadeReal(new BigDecimal("10"));
        entity.setQuantidadeReservada(BigDecimal.ZERO); entity.setVersao(0L);
        when(saldos.findByProdutoId(7L)).thenAnswer(i -> Optional.of(entity));
        when(saldos.saveAndFlush(any())).thenAnswer(i -> { entity = i.getArgument(0); return entity; });
        var config = new EstoqueConfig();
        var controller = config.estoqueControllerClean(config.ajustarInventarioUseCase(commands),
                config.registrarEntradaUseCase(commands), config.buscarEstoquePorIdUseCase(new EstoqueQueryGatewayImpl(saldos)));
        mvc = MockMvcBuilders.standaloneSetup(new EstoqueSpringWebController(controller, Mappers.getMapper(EstoqueWebMapper.class)))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test void reservarEConsumirGeraMovimentacoesEAtualizaSaldo() {
        var itens = List.of(new ItemOSProduto(1L, 7L, new BigDecimal("2"), new BigDecimal("50")));
        new OrcamentoAprovadoListener(commands).reservarItensNoEstoque(new OrcamentoAprovadoEvent(9L, itens));
        assertThat(entity.getQuantidadeReal()).isEqualByComparingTo("10");
        assertThat(entity.getQuantidadeReservada()).isEqualByComparingTo("2");
        new ExecucaoIniciadaListener(commands).consumirItensDoEstoque(new ExecucaoIniciadaEvent(9L, itens));
        assertThat(entity.getQuantidadeReal()).isEqualByComparingTo("8");
        assertThat(entity.getQuantidadeReservada()).isEqualByComparingTo("0");
        verify(movimentos).save(argThat(m -> m.getTipoMovimentacao() == TipoMovimentacao.RESERVA_OS && m.getProdutoId().equals(7L)));
        verify(movimentos).save(argThat(m -> m.getTipoMovimentacao() == TipoMovimentacao.BAIXA_EXECUCAO_OS && m.getQuantidade().compareTo(new BigDecimal("2")) == 0));
    }

    @Test void consultarPorProdutoRegistrarEntradaEAjustar() throws Exception {
        mvc.perform(get("/api/produtos/7")).andExpect(status().isOk()).andExpect(jsonPath("$.produtoId").value(7))
                .andExpect(jsonPath("$.quantidadeReal").value(10));
        mvc.perform(post("/api/produtos/7/estoque/entrada-compra").contentType("application/json")
                .content("{\"quantidadeEntrada\":5,\"observacao\":\"Compra\"}")).andExpect(status().isOk());
        assertThat(entity.getQuantidadeReal()).isEqualByComparingTo("15");
        mvc.perform(post("/api/produtos/7/estoque/ajuste").contentType("application/json")
                .content("{\"quantidade\":12,\"versao\":0,\"observacao\":\"Inventario\"}")).andExpect(status().isOk());
        assertThat(entity.getQuantidadeReal()).isEqualByComparingTo("12");
    }

    @Test void consultaAusenteEQuantidadeInvalidaNaoGravam() throws Exception {
        mvc.perform(get("/api/produtos/99")).andExpect(status().is4xxClientError());
        mvc.perform(post("/api/produtos/7/estoque/entrada-compra").contentType("application/json").content("{\"quantidadeEntrada\":-1}"))
                .andExpect(status().isBadRequest());
        verify(saldos, never()).saveAndFlush(any());
    }

    @Test void salvarSemMovimentoERejeitarEventoSemPrateleira() {
        commands.salvar(new SaldoEstoque(7L), null);
        verifyNoInteractions(movimentos);
        var itens = List.of(new ItemOSProduto(1L, 99L, BigDecimal.ONE, BigDecimal.TEN));
        assertThatThrownBy(() -> new OrcamentoAprovadoListener(commands).reservarItensNoEstoque(new OrcamentoAprovadoEvent(9L, itens)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Prateleira");
        assertThatThrownBy(() -> new ExecucaoIniciadaListener(commands).consumirItensDoEstoque(new ExecucaoIniciadaEvent(9L, itens)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Prateleira");
    }
}
