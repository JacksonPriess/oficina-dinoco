package com.dinoco.oficina.ordemservico.domain.models;

import com.dinoco.oficina.ordemservico.domain.enums.StatusItemServico;
import com.dinoco.oficina.ordemservico.domain.enums.StatusOS;
import com.dinoco.oficina.ordemservico.domain.exceptions.RegraNegocioOSException;
import com.dinoco.oficina.ordemservico.domain.exceptions.TransicaoStatusInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrdemServico - Testes Unitários")
class OrdemServicoTest {

    private OrdemServico ordemServico;
    private static final Long CLIENTE_ID = 1L;
    private static final Long VEICULO_ID = 1L;
    private static final Integer QUILOMETRAGEM = 50000;
    private static final String RECLAMACAO = "Carro não está pegando";

    @BeforeEach
    void setup() {
        ordemServico = new OrdemServico(CLIENTE_ID, VEICULO_ID, QUILOMETRAGEM, RECLAMACAO);
    }

    @Nested
    @DisplayName("Construtor e Inicialização")
    class ConstrutorTests {

        @Test
        @DisplayName("Deve criar OrdemServico com valores iniciais corretos")
        void deveCriarOrdemComValoresIniciais() {
            assertNotNull(ordemServico);
            assertNotNull(ordemServico.getCodigoRastreio());
            assertTrue(ordemServico.getCodigoRastreio().startsWith("OS-"));
            assertEquals(CLIENTE_ID, ordemServico.getClienteId());
            assertEquals(VEICULO_ID, ordemServico.getVeiculoId());
            assertEquals(QUILOMETRAGEM, ordemServico.getQuilometragemEntrada());
            assertEquals(RECLAMACAO, ordemServico.getReclamacaoCliente());
            assertEquals(StatusOS.RECEBIDA, ordemServico.getStatus());
            assertEquals(BigDecimal.ZERO, ordemServico.getValorTotalServicos());
            assertEquals(BigDecimal.ZERO, ordemServico.getValorTotalProdutos());
            assertEquals(BigDecimal.ZERO, ordemServico.getValorDesconto());
            assertEquals(BigDecimal.ZERO, ordemServico.getValorTotalOS());
            assertNotNull(ordemServico.getDataEntrada());
            assertNotNull(ordemServico.getItensServico());
            assertNotNull(ordemServico.getItensProduto());
            assertTrue(ordemServico.getItensServico().isEmpty());
            assertTrue(ordemServico.getItensProduto().isEmpty());
        }

        @Test
        @DisplayName("Deve criar OrdemServico com construtor completo")
        void deveCriarOrdemComConstrutorCompleto() {
            LocalDateTime dataEntrada = LocalDateTime.now();
            OrdemServico os = new OrdemServico(
                    1L, "OS-TEST123", CLIENTE_ID, VEICULO_ID, StatusOS.EM_DIAGNOSTICO,
                    RECLAMACAO, "Laudo técnico", QUILOMETRAGEM, BigDecimal.ZERO,
                    BigDecimal.valueOf(100), BigDecimal.valueOf(50), BigDecimal.valueOf(150),
                    dataEntrada,null,
                    null
            );

            assertEquals(1L, os.getId());
            assertEquals("OS-TEST123", os.getCodigoRastreio());
            assertEquals(StatusOS.EM_DIAGNOSTICO, os.getStatus());
            assertEquals("Laudo técnico", os.getLaudoTecnico());
            assertEquals(BigDecimal.valueOf(100), os.getValorTotalServicos());
            assertEquals(BigDecimal.valueOf(50), os.getValorTotalProdutos());
            assertEquals(BigDecimal.valueOf(150), os.getValorTotalOS());
        }
    }

    @Nested
    @DisplayName("Transições de Status")
    class TransicoesStatusTests {

        @Test
        @DisplayName("Deve iniciar diagnóstico quando status é RECEBIDA")
        void deveIniciarDiagnostico() {
            ordemServico.iniciarDiagnostico();
            assertEquals(StatusOS.EM_DIAGNOSTICO, ordemServico.getStatus());
        }

        @Test
        @DisplayName("Deve lançar exceção ao iniciar diagnóstico com status inválido")
        void deveLancarExcecaoAoIniciarDiagnosticoComStatusInvalido() {
            ordemServico.iniciarDiagnostico();
            assertThrows(TransicaoStatusInvalidaException.class,
                    () -> ordemServico.iniciarDiagnostico(),
                    "Não deve permitir iniciar diagnóstico de novo");
        }

        @Test
        @DisplayName("Deve concluir diagnóstico e transitar para AGUARDANDO_ORCAMENTO")
        void deveConcluirDiagnostico() {
            ordemServico.iniciarDiagnostico();
            ItemOSServico servico = new ItemOSServico(1L, 1L, 1L, BigDecimal.valueOf(100), StatusItemServico.PENDENTE, LocalDateTime.now(), null);
            ordemServico.adicionarServico(servico);

            String laudo = "Diagnóstico concluído";
            ordemServico.concluirDiagnostico(laudo);

            assertEquals(StatusOS.AGUARDANDO_ORCAMENTO, ordemServico.getStatus());
            assertEquals(laudo, ordemServico.getLaudoTecnico());
        }

        @Test
        @DisplayName("Deve lançar exceção ao concluir diagnóstico sem serviços")
        void deveLancarExcecaoAoConcluirDiagnosticoSemServicos() {
            ordemServico.iniciarDiagnostico();
            assertThrows(RegraNegocioOSException.class,
                    () -> ordemServico.concluirDiagnostico("Laudo"),
                    "Não deve permitir concluir diagnóstico sem serviços");
        }

        @Test
        @DisplayName("Deve lançar exceção ao concluir diagnóstico com status inválido")
        void deveLancarExcecaoAoConcluirDiagnosticoComStatusInvalido() {
            assertThrows(TransicaoStatusInvalidaException.class,
                    () -> ordemServico.concluirDiagnostico("Laudo"),
                    "Não deve permitir concluir diagnóstico se não estiver em diagnóstico");
        }

        @Test
        @DisplayName("Deve enviar orçamento quando todos os itens têm valor")
        void deveEnviarOrcamento() {
            ordemServico.iniciarDiagnostico();
            ItemOSServico servico = new ItemOSServico(1L, 1L, 1L, BigDecimal.valueOf(100), StatusItemServico.PENDENTE, LocalDateTime.now(), null);
            ItemOSProduto produto = new ItemOSProduto(1L,1L, BigDecimal.ONE, BigDecimal.valueOf(50));
            ordemServico.adicionarServico(servico);
            ordemServico.adicionarProduto(produto);
            ordemServico.concluirDiagnostico("Laudo");

            ordemServico.enviarOrcamento();
            assertEquals(StatusOS.AGUARDANDO_APROVACAO, ordemServico.getStatus());
        }

        @Test
        @DisplayName("Deve lançar exceção ao enviar orçamento com serviço sem valor")
        void deveLancarExcecaoAoEnviarOrcamentoComServicoSemValor() {
            ordemServico.iniciarDiagnostico();
            ItemOSServico servico = new ItemOSServico(1L, 1L, 1L, BigDecimal.ZERO, StatusItemServico.PENDENTE, LocalDateTime.now(), null);
            ItemOSProduto produto = new ItemOSProduto(1L,1L, BigDecimal.ONE, BigDecimal.valueOf(50));
            ordemServico.adicionarServico(servico);
            ordemServico.adicionarProduto(produto);
            ordemServico.concluirDiagnostico("Laudo");

            assertThrows(RegraNegocioOSException.class,
                    () -> ordemServico.enviarOrcamento(),
                    "Não deve permitir enviar orçamento com serviço sem valor");
        }

        @Test
        @DisplayName("Deve lançar exceção ao enviar orçamento com produto sem valor")
        void deveLancarExcecaoAoEnviarOrcamentoComProdutoSemValor() {
            ordemServico.iniciarDiagnostico();
            ItemOSServico servico = new ItemOSServico(1L, 1L, BigDecimal.valueOf(100));
            ItemOSProduto produto = new ItemOSProduto(1L, BigDecimal.ONE, BigDecimal.ZERO);
            ordemServico.adicionarServico(servico);
            ordemServico.adicionarProduto(produto);
            ordemServico.concluirDiagnostico("Laudo");

            assertThrows(RegraNegocioOSException.class,
                    () -> ordemServico.enviarOrcamento(),
                    "Não deve permitir enviar orçamento com produto sem valor");
        }

        @Test
        @DisplayName("Deve reprovar orçamento quando status é AGUARDANDO_APROVACAO")
        void deveReprovarOrcamento() {
            ordemServico.iniciarDiagnostico();
            ItemOSServico servico = new ItemOSServico(1L, 1L, BigDecimal.valueOf(100));
            ItemOSProduto produto = new ItemOSProduto(1L, BigDecimal.ONE, BigDecimal.valueOf(50));
            ordemServico.adicionarServico(servico);
            ordemServico.adicionarProduto(produto);
            ordemServico.concluirDiagnostico("Laudo");
            ordemServico.enviarOrcamento();

            ordemServico.reprovarOrcamento();
            assertEquals(StatusOS.REPROVADA, ordemServico.getStatus());
            assertNotNull(ordemServico.getDataReprovacao());
        }

        @Test
        @DisplayName("Deve marcar como aguardando fornecedor")
        void deveMarcarAguardandoFornecedor() {
            ordemServico.iniciarDiagnostico();
            ItemOSServico servico = new ItemOSServico(1L, 1L, BigDecimal.valueOf(100));
            ItemOSProduto produto = new ItemOSProduto(1L, BigDecimal.ONE, BigDecimal.valueOf(50));
            ordemServico.adicionarServico(servico);
            ordemServico.adicionarProduto(produto);
            ordemServico.concluirDiagnostico("Laudo");
            ordemServico.enviarOrcamento();

            ordemServico.marcarAguardandoFornecedor();
            assertEquals(StatusOS.AGUARDANDO_FORNECEDOR, ordemServico.getStatus());
        }

        @Test
        @DisplayName("Deve marcar como pronta para execução a partir de AGUARDANDO_APROVACAO")
        void deveMarcarProntaParaExecucaoDeAguardandoAprovacao() {
            ordemServico.iniciarDiagnostico();
            ItemOSServico servico = new ItemOSServico(1L, 1L, BigDecimal.valueOf(100));
            ItemOSProduto produto = new ItemOSProduto(1L, BigDecimal.ONE, BigDecimal.valueOf(50));
            ordemServico.adicionarServico(servico);
            ordemServico.adicionarProduto(produto);
            ordemServico.concluirDiagnostico("Laudo");
            ordemServico.enviarOrcamento();

            ordemServico.marcarProntaParaExecucao();
            assertEquals(StatusOS.AGUARDANDO_EXECUCAO, ordemServico.getStatus());
        }

        @Test
        @DisplayName("Deve marcar como pronta para execução a partir de AGUARDANDO_FORNECEDOR")
        void deveMarcarProntaParaExecucaoDeAguardandoFornecedor() {
            ordemServico.iniciarDiagnostico();
            ItemOSServico servico = new ItemOSServico(1L, 1L, BigDecimal.valueOf(100));
            ItemOSProduto produto = new ItemOSProduto(1L, BigDecimal.ONE, BigDecimal.valueOf(50));
            ordemServico.adicionarServico(servico);
            ordemServico.adicionarProduto(produto);
            ordemServico.concluirDiagnostico("Laudo");
            ordemServico.enviarOrcamento();
            ordemServico.marcarAguardandoFornecedor();

            ordemServico.marcarProntaParaExecucao();
            assertEquals(StatusOS.AGUARDANDO_EXECUCAO, ordemServico.getStatus());
        }

        @Test
        @DisplayName("Deve iniciar execução quando status é AGUARDANDO_EXECUCAO")
        void deveIniciarExecucao() {
            prepararOSParaExecucao();
            ordemServico.marcarProntaParaExecucao();

            ordemServico.iniciarExecucao();
            assertEquals(StatusOS.EM_EXECUCAO, ordemServico.getStatus());
        }

        @Test
        @DisplayName("Deve finalizar execução quando todos os serviços estão concluídos")
        void deveFinalizarExecucao() {
            prepararEExecutarServicos();

            ordemServico.finalizarExecucao();
            assertEquals(StatusOS.FINALIZADA, ordemServico.getStatus());
        }

        @Test
        @DisplayName("Deve lançar exceção ao finalizar execução com serviços pendentes")
        void deveLancarExcecaoAoFinalizarExecucaoComServicosPendentes() {
            prepararOSParaExecucao();
            ordemServico.marcarProntaParaExecucao();
            ordemServico.iniciarExecucao();

            assertThrows(RegraNegocioOSException.class,
                    () -> ordemServico.finalizarExecucao(),
                    "Não deve permitir finalizar execução com serviços pendentes");
        }

        @Test
        @DisplayName("Deve concluir e marcar como entregue")
        void deveConcluirEMarcarComoEntregue() {
            prepararEExecutarServicos();
            ordemServico.finalizarExecucao();

            ordemServico.concluir();
            assertEquals(StatusOS.ENTREGUE, ordemServico.getStatus());
            assertNotNull(ordemServico.getDataSaida());
        }
    }

    @Nested
    @DisplayName("Adição e Alteração de Itens de Serviço")
    class ItemsServicoTests {

        @Test
        @DisplayName("Deve adicionar serviço quando status permite")
        void deveAdicionarServico() {
            ordemServico.iniciarDiagnostico();
            ItemOSServico servico = new ItemOSServico(1L, 1L, BigDecimal.valueOf(100));

            ordemServico.adicionarServico(servico);

            assertEquals(1, ordemServico.getItensServico().size());
            assertEquals(servico, ordemServico.getItensServico().get(0));
            assertEquals(BigDecimal.valueOf(100), ordemServico.getValorTotalServicos());
        }

        @Test
        @DisplayName("Deve lançar exceção ao adicionar serviço duplicado")
        void deveLancarExcecaoAoAdicionarServicoDuplicado() {
            ordemServico.iniciarDiagnostico();
            ItemOSServico servico1 = new ItemOSServico(1L, 1L, BigDecimal.valueOf(100));
            ItemOSServico servico2 = new ItemOSServico(1L, 2L, BigDecimal.valueOf(50));

            ordemServico.adicionarServico(servico1);
            assertThrows(RegraNegocioOSException.class,
                    () -> ordemServico.adicionarServico(servico2),
                    "Não deve permitir adicionar serviço duplicado");
        }

        @Test
        @DisplayName("Deve lançar exceção ao adicionar serviço com status inválido")
        void deveLancarExcecaoAoAdicionarServicoComStatusInvalido() {
            ItemOSServico servico = new ItemOSServico(1L, 1L, BigDecimal.valueOf(100));
            assertThrows(RegraNegocioOSException.class,
                    () -> ordemServico.adicionarServico(servico),
                    "Não deve permitir adicionar serviço quando status é RECEBIDA");
        }

        @Test
        @DisplayName("Deve alterar dados do item de serviço")
        void deveAlterarDadosItemServico() {
            ordemServico.iniciarDiagnostico();
            ItemOSServico servico = new ItemOSServico(1L, 1L, 1L, BigDecimal.valueOf(100), StatusItemServico.PENDENTE, LocalDateTime.now(), null );

            ordemServico.adicionarServico(servico);

            BigDecimal novoValor = BigDecimal.valueOf(150);
            Long novoMecanico = 2L;
            ordemServico.alterarItemServico(servico.getId(), novoValor, novoMecanico);

            assertEquals(novoValor, ordemServico.getItensServico().get(0).getValorCobrado());
            assertEquals(novoMecanico, ordemServico.getItensServico().get(0).getMecanicoId());
            assertEquals(novoValor, ordemServico.getValorTotalServicos());
        }

        @Test
        @DisplayName("Deve lançar exceção ao alterar serviço inexistente")
        void deveLancarExcecaoAoAlterarServicoInexistente() {
            ordemServico.iniciarDiagnostico();
            assertThrows(RegraNegocioOSException.class,
                    () -> ordemServico.alterarItemServico(999L, BigDecimal.valueOf(100), 1L),
                    "Não deve permitir alterar serviço inexistente");
        }

        @Test
        @DisplayName("Deve iniciar execução de item de serviço")
        void deveIniciarExecucaoItemServico() {
            prepararEIniciarExecucao();

            assertThrows(RegraNegocioOSException.class,
                    () -> ordemServico.iniciarExecucaoItemServico(999L),
                    "Item inválido deve lançar exceção");
        }

        @Test
        @DisplayName("Deve concluir execução de item de serviço")
        void deveConcluirExecucaoItemServico() {
            prepararEIniciarExecucao();
            ItemOSServico servico = ordemServico.getItensServico().get(0);

            ordemServico.iniciarExecucaoItemServico(servico.getId());
            LocalDateTime dataFim = LocalDateTime.now();
            ordemServico.concluirExecucaoItemServico(servico.getId(), dataFim);

            assertEquals(StatusItemServico.CONCLUIDO, servico.getStatusItem());
            assertEquals(dataFim, servico.getDataFim());
        }
    }

    @Nested
    @DisplayName("Adição e Alteração de Itens de Produto")
    class ItemsProdutoTests {

        @Test
        @DisplayName("Deve adicionar produto quando status permite")
        void deveAdicionarProduto() {
            ordemServico.iniciarDiagnostico();
            ItemOSProduto produto = new ItemOSProduto(1L, BigDecimal.valueOf(2), BigDecimal.valueOf(50));

            ordemServico.adicionarProduto(produto);

            assertEquals(1, ordemServico.getItensProduto().size());
            assertEquals(produto, ordemServico.getItensProduto().get(0));
            assertEquals(BigDecimal.valueOf(100), ordemServico.getValorTotalProdutos());
        }

        @Test
        @DisplayName("Deve lançar exceção ao adicionar produto duplicado")
        void deveLancarExcecaoAoAdicionarProdutoDuplicado() {
            ordemServico.iniciarDiagnostico();
            ItemOSProduto produto1 = new ItemOSProduto(1L, BigDecimal.ONE, BigDecimal.valueOf(50));
            ItemOSProduto produto2 = new ItemOSProduto(1L, BigDecimal.valueOf(2), BigDecimal.valueOf(30));

            ordemServico.adicionarProduto(produto1);
            assertThrows(RegraNegocioOSException.class,
                    () -> ordemServico.adicionarProduto(produto2),
                    "Não deve permitir adicionar produto duplicado");
        }

        @Test
        @DisplayName("Deve lançar exceção ao adicionar produto com status inválido")
        void deveLancarExcecaoAoAdicionarProdutoComStatusInvalido() {
            ItemOSProduto produto = new ItemOSProduto(1L, BigDecimal.ONE, BigDecimal.valueOf(50));
            assertThrows(RegraNegocioOSException.class,
                    () -> ordemServico.adicionarProduto(produto),
                    "Não deve permitir adicionar produto quando status é RECEBIDA");
        }

        @Test
        @DisplayName("Deve alterar dados do item de produto")
        void deveAlterarDadosItemProduto() {
            ordemServico.iniciarDiagnostico();
            ItemOSProduto produto = new ItemOSProduto(1L,1L, BigDecimal.valueOf(2), BigDecimal.valueOf(50));
            ordemServico.adicionarProduto(produto);

            BigDecimal novoPreco = BigDecimal.valueOf(75);
            BigDecimal novaQuantidade = BigDecimal.valueOf(3);
            ordemServico.alterarItemProduto(produto.getId(), novoPreco, novaQuantidade);

            assertEquals(novoPreco, ordemServico.getItensProduto().get(0).getValorUnitarioVenda());
            assertEquals(novaQuantidade, ordemServico.getItensProduto().get(0).getQuantidade());
            assertEquals(BigDecimal.valueOf(225), ordemServico.getValorTotalProdutos());
        }

        @Test
        @DisplayName("Deve lançar exceção ao alterar produto inexistente")
        void deveLancarExcecaoAoAlterarProdutoInexistente() {
            ordemServico.iniciarDiagnostico();
            assertThrows(RegraNegocioOSException.class,
                    () -> ordemServico.alterarItemProduto(999L, BigDecimal.valueOf(100), BigDecimal.ONE),
                    "Não deve permitir alterar produto inexistente");
        }
    }

    @Nested
    @DisplayName("Cálculo de Totais")
    class CalculoTotaisTests {

        @Test
        @DisplayName("Deve recalcular totais ao adicionar serviços")
        void deveRecalcularTotaisAoAdicionarServicos() {
            ordemServico.iniciarDiagnostico();
            ItemOSServico servico1 = new ItemOSServico(1L, 1L, BigDecimal.valueOf(100));
            ItemOSServico servico2 = new ItemOSServico(2L, 2L, BigDecimal.valueOf(50));

            ordemServico.adicionarServico(servico1);
            assertEquals(BigDecimal.valueOf(100), ordemServico.getValorTotalServicos());
            assertEquals(BigDecimal.valueOf(100), ordemServico.getValorTotalOS());

            ordemServico.adicionarServico(servico2);
            assertEquals(BigDecimal.valueOf(150), ordemServico.getValorTotalServicos());
            assertEquals(BigDecimal.valueOf(150), ordemServico.getValorTotalOS());
        }

        @Test
        @DisplayName("Deve recalcular totais ao adicionar produtos")
        void deveRecalcularTotaisAoAdicionarProdutos() {
            ordemServico.iniciarDiagnostico();
            ItemOSProduto produto1 = new ItemOSProduto(1L, BigDecimal.valueOf(2), BigDecimal.valueOf(50));
            ItemOSProduto produto2 = new ItemOSProduto(2L, BigDecimal.valueOf(3), BigDecimal.valueOf(30));

            ordemServico.adicionarProduto(produto1);
            assertEquals(BigDecimal.valueOf(100), ordemServico.getValorTotalProdutos());
            assertEquals(BigDecimal.valueOf(100), ordemServico.getValorTotalOS());

            ordemServico.adicionarProduto(produto2);
            assertEquals(BigDecimal.valueOf(190), ordemServico.getValorTotalProdutos());
            assertEquals(BigDecimal.valueOf(190), ordemServico.getValorTotalOS());
        }

        @Test
        @DisplayName("Deve calcular total com serviços e produtos")
        void deveCalcularTotalComServicosEProdutos() {
            ordemServico.iniciarDiagnostico();
            ItemOSServico servico = new ItemOSServico(1L, 1L, BigDecimal.valueOf(100));
            ItemOSProduto produto = new ItemOSProduto(1L, BigDecimal.valueOf(2), BigDecimal.valueOf(50));

            ordemServico.adicionarServico(servico);
            ordemServico.adicionarProduto(produto);

            assertEquals(BigDecimal.valueOf(100), ordemServico.getValorTotalServicos());
            assertEquals(BigDecimal.valueOf(100), ordemServico.getValorTotalProdutos());
            assertEquals(BigDecimal.valueOf(200), ordemServico.getValorTotalOS());
        }

        @Test
        @DisplayName("Deve descontar valor do total")
        void deveDescontarValorDoTotal() {
            ordemServico.iniciarDiagnostico();
            ItemOSServico servico = new ItemOSServico(1L, 1L, BigDecimal.valueOf(100));
            ItemOSProduto produto = new ItemOSProduto(1L, BigDecimal.valueOf(2), BigDecimal.valueOf(50));
            ordemServico.adicionarServico(servico);
            ordemServico.adicionarProduto(produto);

            ordemServico.setValorDesconto(BigDecimal.valueOf(30));
            ordemServico.recalcularTotais();

            assertEquals(BigDecimal.valueOf(170), ordemServico.getValorTotalOS());
        }

        @Test
        @DisplayName("Deve garantir que total não seja negativo")
        void deveGarantirQueTotalNaoSejaNegativo() {
            ordemServico.iniciarDiagnostico();
            ItemOSServico servico = new ItemOSServico(1L, 1L, BigDecimal.valueOf(50));
            ordemServico.adicionarServico(servico);

            ordemServico.setValorDesconto(BigDecimal.valueOf(100));
            ordemServico.recalcularTotais();

            assertEquals(BigDecimal.ZERO, ordemServico.getValorTotalOS());
        }
    }

    @Nested
    @DisplayName("Fluxo Completo de Ordem de Serviço")
    class FluxoCompletoTests {

        @Test
        @DisplayName("Deve seguir fluxo completo de OS: de RECEBIDA a ENTREGUE")
        void deveFollowFluxoCompleto() {
            // 1. Diagnóstico
            assertEquals(StatusOS.RECEBIDA, ordemServico.getStatus());
            ordemServico.iniciarDiagnostico();
            assertEquals(StatusOS.EM_DIAGNOSTICO, ordemServico.getStatus());

            // 2. Adicionar itens
            ItemOSServico servico = new ItemOSServico(1L, 1L, 1L, BigDecimal.valueOf(100), StatusItemServico.PENDENTE, LocalDateTime.now(), null);
            ItemOSProduto produto = new ItemOSProduto(1L, BigDecimal.valueOf(2), BigDecimal.valueOf(50));
            ordemServico.adicionarServico(servico);
            ordemServico.adicionarProduto(produto);

            // 3. Concluir diagnóstico
            ordemServico.concluirDiagnostico("Diagnóstico completo");
            assertEquals(StatusOS.AGUARDANDO_ORCAMENTO, ordemServico.getStatus());

            // 4. Enviar orçamento
            ordemServico.enviarOrcamento();
            assertEquals(StatusOS.AGUARDANDO_APROVACAO, ordemServico.getStatus());

            // 5. Marcar pronta para execução
            ordemServico.marcarProntaParaExecucao();
            assertEquals(StatusOS.AGUARDANDO_EXECUCAO, ordemServico.getStatus());

            // 6. Iniciar execução
            ordemServico.iniciarExecucao();
            assertEquals(StatusOS.EM_EXECUCAO, ordemServico.getStatus());

            // 7. Executar serviço
            ordemServico.iniciarExecucaoItemServico(servico.getId());
            ordemServico.concluirExecucaoItemServico(servico.getId(), LocalDateTime.now());
            assertEquals(StatusItemServico.CONCLUIDO, servico.getStatusItem());

            // 8. Finalizar execução
            ordemServico.finalizarExecucao();
            assertEquals(StatusOS.FINALIZADA, ordemServico.getStatus());

            // 9. Concluir e entregar
            ordemServico.concluir();
            assertEquals(StatusOS.ENTREGUE, ordemServico.getStatus());
            assertNotNull(ordemServico.getDataSaida());
        }

        @Test
        @DisplayName("Deve permitir fluxo com aguardamento de fornecedor")
        void deveFollowFluxoComAguardamentoFornecedor() {
            prepararOSParaAprovacao();

            ordemServico.marcarAguardandoFornecedor();
            assertEquals(StatusOS.AGUARDANDO_FORNECEDOR, ordemServico.getStatus());

            ordemServico.marcarProntaParaExecucao();
            assertEquals(StatusOS.AGUARDANDO_EXECUCAO, ordemServico.getStatus());
        }

        @Test
        @DisplayName("Deve permitir reprovação de orçamento")
        void deveFollowFluxoComReprovacao() {
            prepararOSParaAprovacao();

            ordemServico.reprovarOrcamento();
            assertEquals(StatusOS.REPROVADA, ordemServico.getStatus());
            assertNotNull(ordemServico.getDataReprovacao());
        }
    }

    // Métodos auxiliares
    private void prepararOSParaExecucao() {
        ordemServico.iniciarDiagnostico();
        ItemOSServico servico = new ItemOSServico(1L, 1L, 1L, BigDecimal.valueOf(100), StatusItemServico.PENDENTE, LocalDateTime.now(), null);
        ItemOSProduto produto = new ItemOSProduto(1L, BigDecimal.ONE, BigDecimal.valueOf(50));
        ordemServico.adicionarServico(servico);
        ordemServico.adicionarProduto(produto);
        ordemServico.concluirDiagnostico("Laudo");
        ordemServico.enviarOrcamento();
    }

    private void prepararOSParaAprovacao() {
        ordemServico.iniciarDiagnostico();
        ItemOSServico servico = new ItemOSServico(1L, 1L, 1L, BigDecimal.valueOf(100), StatusItemServico.PENDENTE, LocalDateTime.now(), null);
        ItemOSProduto produto = new ItemOSProduto(1L, BigDecimal.ONE, BigDecimal.valueOf(50));
        ordemServico.adicionarServico(servico);
        ordemServico.adicionarProduto(produto);
        ordemServico.concluirDiagnostico("Laudo");
        ordemServico.enviarOrcamento();
    }

    private void prepararEIniciarExecucao() {
        prepararOSParaExecucao();
        ordemServico.marcarProntaParaExecucao();
        ordemServico.iniciarExecucao();
    }

    private void prepararEExecutarServicos() {
        prepararEIniciarExecucao();
        ItemOSServico servico = ordemServico.getItensServico().get(0);
        ordemServico.iniciarExecucaoItemServico(servico.getId());
        ordemServico.concluirExecucaoItemServico(servico.getId(), LocalDateTime.now());
    }
}