package com.dinoco.oficina.service;

import com.dinoco.oficina.dto.LinkWhatsAppDto;
import com.dinoco.oficina.dto.OrdemServicoDetalhadaResponseDto;
import com.dinoco.oficina.dto.OrdemServicoResponseDto;
import com.dinoco.oficina.entity.*;
import com.dinoco.oficina.enums.StatusItemServico;
import com.dinoco.oficina.enums.StatusOS;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.helper.ClienteBuilder;
import com.dinoco.oficina.helper.OrdemServicoBuilder;
import com.dinoco.oficina.helper.OrdemServicoRequestDtoBuilder;
import com.dinoco.oficina.helper.VeiculoBuilder;
import com.dinoco.oficina.repository.OrdemServicoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdemServicoServiceTest {

    @InjectMocks
    private OrdemServicoService osService;

    @Mock
    private OrdemServicoRepository repository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private MovimentacaoEstoqueService movimentacaoEstoqueService;

    @Mock
    private VeiculoService veiculoService;

    @Captor
    private ArgumentCaptor<OrdemServico> osCaptor;

    @Test
    void deveAbrirOrdemServicoQuandoDadosForemValidos() {
        // 1. Arrange
        var requestDto = OrdemServicoRequestDtoBuilder.umRequest().build();

        var clienteMock = ClienteBuilder.umCliente();
        var veiculoMock = VeiculoBuilder.umVeiculo().build();
        var osSalvaMock = OrdemServicoBuilder.umaOrdemServico().build();

        when(clienteService.buscarEntidadePorId(requestDto.clienteId())).thenReturn(clienteMock);
        when(veiculoService.buscarEntidadePorId(requestDto.veiculoId())).thenReturn(veiculoMock);
        when(repository.save(any(OrdemServico.class))).thenReturn(osSalvaMock);
        // 2. Act
        OrdemServicoResponseDto response = osService.abrirOs(requestDto);
        // 3. Assert
        assertNotNull(response);
        verify(repository, times(1)).save(osCaptor.capture());
        OrdemServico entidadeCapturada = osCaptor.getValue();

        assertEquals(clienteMock, entidadeCapturada.getCliente());
        assertEquals(veiculoMock, entidadeCapturada.getVeiculo());
        assertEquals(requestDto.quilometragemEntrada(), entidadeCapturada.getQuilometragemEntrada());
        assertEquals(requestDto.reclamacaoCliente(), entidadeCapturada.getReclamacaoCliente());

        assertEquals(StatusOS.RECEBIDA, entidadeCapturada.getStatus());

        assertEquals(0, BigDecimal.ZERO.compareTo(entidadeCapturada.getValorTotalServicos()));
        assertEquals(0, BigDecimal.ZERO.compareTo(entidadeCapturada.getValorTotalProdutos()));
        assertEquals(0, BigDecimal.ZERO.compareTo(entidadeCapturada.getValorDesconto()));
        assertEquals(0, BigDecimal.ZERO.compareTo(entidadeCapturada.getValorTotalOS()));
        assertNotNull(entidadeCapturada.getDataEntrada());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando o Cliente não existir")
    void deveLancarExceptionQuandoClienteNaoExistir() {
        // 1. Arrange
        Long clienteIdInvalido = 99L;
        var requestDto = OrdemServicoRequestDtoBuilder.umRequest()
                .comClienteId(clienteIdInvalido)
                .build();
        when(clienteService.buscarEntidadePorId(clienteIdInvalido))
                .thenThrow(new RecursoNaoEncontradoException("Cliente não encontrado com ID: " + clienteIdInvalido));
        // 2. Act & Assert
        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> osService.abrirOs(requestDto)
        );
        assertEquals("Cliente não encontrado com ID: 99", exception.getMessage());
        verify(veiculoService, never()).buscarEntidadePorId(anyLong());
        verify(repository, never()).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve iniciar o diagnóstico e alterar o status para EM_DIAGNOSTICO")
    void deveIniciarDiagnosticoComSucesso() {
        // 1. Arrange
        Long osId = 1L;
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();
        osMock.setStatus(StatusOS.RECEBIDA);
        when(repository.findById(osId)).thenReturn(Optional.of(osMock));
        // 2. Act
        osService.iniciarDiagnostico(osId);
        // 3. Assert
        verify(repository, times(1)).save(osCaptor.capture());
        OrdemServico entidadeCapturada = osCaptor.getValue();
        assertEquals(StatusOS.EM_DIAGNOSTICO, entidadeCapturada.getStatus());
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException quando a OS não estiver com status RECEBIDA")
    void deveLancarExceptionQuandoStatusNaoForRecebidaParaDiagnostico() {
        // 1. Arrange
        Long osId = 1L;
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();
        osMock.setStatus(StatusOS.FINALIZADA);
        when(repository.findById(osId)).thenReturn(Optional.of(osMock));
        // 2. Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> osService.iniciarDiagnostico(osId)
        );
        assertEquals("Para iniciar um diagnóstico a OS deve estar com status RECEBIDA. Status atual da OS: FINALIZADA", exception.getMessage());
        verify(repository, never()).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve concluir o diagnóstico, preencher o laudo e alterar status para AGUARDANDO_ORCAMENTO")
    void deveConcluirDiagnosticoComSucesso() {
        // 1. Arrange
        Long osId = 1L;
        String laudoTecnicoValido = "Identificado vazamento na junta do cabeçote e necessidade de retífica.";
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();
        osMock.setStatus(StatusOS.EM_DIAGNOSTICO);

        ItemOSServico itemMock = new ItemOSServico();
        itemMock.setValorCobrado(new BigDecimal("150.00"));
        osMock.setItensServico(Set.of(itemMock));
        when(repository.findById(osId)).thenReturn(Optional.of(osMock));
        // 2. Act
        osService.concluirDiagnostico(osId, laudoTecnicoValido);
        // 3. Assert
        verify(repository, times(1)).save(osCaptor.capture());
        OrdemServico entidadeCapturada = osCaptor.getValue();
        assertEquals(StatusOS.AGUARDANDO_ORCAMENTO, entidadeCapturada.getStatus());
        assertEquals(laudoTecnicoValido, entidadeCapturada.getLaudoTecnico());
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException ao concluir diagnóstico se OS não estiver EM_DIAGNOSTICO")
    void deveLancarExceptionAoConcluirDiagnosticoComStatusInvalido() {
        // 1. Arrange
        Long osId = 1L;
        String laudoTecnicoValido = "Identificado vazamento na junta do cabeçote e necessidade de retífica.";
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();
        osMock.setStatus(StatusOS.RECEBIDA);
        when(repository.findById(osId)).thenReturn(Optional.of(osMock));
        // 2. Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> osService.concluirDiagnostico(osId, laudoTecnicoValido)
        );
        assertEquals("Operação inválida para o status atual da OS: RECEBIDA", exception.getMessage());
        verify(repository, never()).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException ao concluir diagnóstico de uma OS sem itens de serviço")
    void deveLancarExceptionAoConcluirDiagnosticoSemItens() {
        // 1. Arrange
        Long osId = 1L;
        String laudoTecnico = "Laudo técnico padrão";

        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();
        osMock.setStatus(StatusOS.EM_DIAGNOSTICO);
        osMock.setItensServico(Collections.EMPTY_SET);
        when(repository.findById(osId)).thenReturn(Optional.of(osMock));

        // 2. Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> osService.concluirDiagnostico(osId, laudoTecnico)
        );

        assertEquals("Para concluir o diagnóstico, a OS deve possuir ao menos um item de serviço.", exception.getMessage());
        verify(repository, never()).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve enviar orçamento, alterar status para AGUARDANDO_APROVACAO e retornar link do WhatsApp")
    void deveEnviarOrcamentoEGerarLinkWhatsAppComSucesso() {
        // 1. Arrange
        Long osId = 1L;
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();

        var cliente = ClienteBuilder.umCliente();
        cliente.setNome("Jackson");
        cliente.setTelefone("(47) 988733271");
        osMock.setCliente(cliente);
        osMock.setValorTotalOS(new BigDecimal("1500.00"));

        ItemOSServico servicoMock = new ItemOSServico();
        servicoMock.setValorCobrado(new BigDecimal("500.00"));
        osMock.setItensServico(Set.of(servicoMock));

        ItemOSProduto produtoMock = new ItemOSProduto();
        produtoMock.setValorUnitarioVenda(new BigDecimal("1000.00"));
        osMock.setItensProduto(Set.of(produtoMock));

        when(repository.findById(osId)).thenReturn(Optional.of(osMock));

        // 2. Act
        LinkWhatsAppDto response = osService.enviarOrcamento(osId);

        // 3. Assert - Estado da OS
        verify(repository, times(1)).save(osCaptor.capture());
        OrdemServico entidadeCapturada = osCaptor.getValue();
        assertEquals(StatusOS.AGUARDANDO_APROVACAO, entidadeCapturada.getStatus());

        // 4. Assert - Validação do Link do WhatsApp
        assertNotNull(response);
        String urlGerada = response.urlWhatsApp();
        String urlDecodificada = URLDecoder.decode(urlGerada, StandardCharsets.UTF_8);
        assertTrue(urlDecodificada.contains("wa.me/5547988733271"));

        // 2. Verifica as partes principais da mensagem (ignorando o tipo de espaço do R$)
        assertTrue(urlDecodificada.contains("Olá Jackson"));
        assertTrue(urlDecodificada.contains("O orçamento do seu veículo está pronto!"));
        assertTrue(urlDecodificada.contains("Total de R$"));
        assertTrue(urlDecodificada.contains("1.500,00"));
        assertTrue(urlDecodificada.contains("Podemos dar andamento"));
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException quando existir item de serviço com valor zero")
    void deveLancarExceptionQuandoItemServicoNaoTiverValor() {
        // 1. Arrange
        Long osId = 1L;
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();

        ItemOSServico servicoInvalido = new ItemOSServico();
        servicoInvalido.setValorCobrado(BigDecimal.ZERO);
        osMock.setItensServico(Set.of(servicoInvalido));

        ItemOSProduto produtoMock = new ItemOSProduto();
        produtoMock.setValorUnitarioVenda(new BigDecimal("1000.00"));
        osMock.setItensProduto(Set.of(produtoMock));
        when(repository.findById(osId)).thenReturn(Optional.of(osMock));
        // 2. Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> osService.enviarOrcamento(osId)
        );
        assertEquals("Existem itens da OS sem valor R$.", exception.getMessage());
        verify(repository, never()).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException quando existir item de produto com valor zero")
    void deveLancarExceptionQuandoItemProdutoNaoTiverValor() {
        // 1. Arrange
        Long osId = 1L;
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();

        ItemOSServico servicoValido = new ItemOSServico();
        servicoValido.setValorCobrado(new BigDecimal("500.00"));
        osMock.setItensServico(Set.of(servicoValido));
        ItemOSProduto produtoInvalido = new ItemOSProduto();
        produtoInvalido.setValorUnitarioVenda(BigDecimal.ZERO);
        osMock.setItensProduto(Set.of(produtoInvalido));
        when(repository.findById(osId)).thenReturn(Optional.of(osMock));
        // 2. Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> osService.enviarOrcamento(osId)
        );
        assertEquals("Existem itens da OS sem valor R$.", exception.getMessage());
        verify(repository, never()).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve reprovar o orçamento, alterar status para REPROVADA e preencher data de reprovação")
    void deveReprovarOrcamentoComSucesso() {
        // 1. Arrange
        Long osId = 1L;
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();
        osMock.setStatus(StatusOS.AGUARDANDO_APROVACAO);
        when(repository.findById(osId)).thenReturn(Optional.of(osMock));
        // 2. Act
        osService.reprovarOrcamento(osId);
        // 3. Assert
        verify(repository, times(1)).save(osCaptor.capture());
        OrdemServico entidadeCapturada = osCaptor.getValue();
        assertEquals(StatusOS.REPROVADA, entidadeCapturada.getStatus());
        assertNotNull(entidadeCapturada.getDataReprovacao());
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException ao tentar reprovar orçamento com status diferente de AGUARDANDO_APROVACAO")
    void deveLancarExceptionAoReprovarOrcamentoComStatusInvalido() {
        // 1. Arrange
        Long osId = 1L;
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();
        osMock.setStatus(StatusOS.EM_DIAGNOSTICO);
        when(repository.findById(osId)).thenReturn(Optional.of(osMock));
        // 2. Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> osService.reprovarOrcamento(osId)
        );
        assertEquals("Operação inválida para o status atual da OS: EM_DIAGNOSTICO", exception.getMessage());
        verify(repository, never()).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve aprovar orçamento e definir status como AGUARDANDO_EXECUCAO quando houver estoque")
    void deveAprovarOrcamentoComEstoqueDisponivel() {
        // 1. Arrange
        Long osId = 1L;
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();
        osMock.setStatus(StatusOS.AGUARDANDO_APROVACAO);

        Produto produtoComEstoque = new Produto();
        produtoComEstoque.setQuantidadeAtual(new BigDecimal(1));

        ItemOSProduto item = new ItemOSProduto();
        item.setQuantidade(new BigDecimal(1));
        item.setProduto(produtoComEstoque);
        osMock.setItensProduto(Set.of(item));
        when(repository.findById(osId)).thenReturn(Optional.of(osMock));
        // 2. Act
        osService.aprovarOrcamento(osId);
        // 3. Assert
        verify(movimentacaoEstoqueService, times(1)).reservarItens(osMock);
        verify(repository).save(osCaptor.capture());
        assertEquals(StatusOS.AGUARDANDO_EXECUCAO, osCaptor.getValue().getStatus());
    }

    @Test
    @DisplayName("Deve aprovar orçamento e definir status como AGUARDANDO_FORNECEDOR quando NÃO houver estoque")
    void deveAprovarOrcamentoSemEstoqueDisponivel() {
        // 1. Arrange
        Long osId = 1L;
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();
        osMock.setStatus(StatusOS.AGUARDANDO_APROVACAO);

        Produto produtoSemEstoque = new Produto();
        produtoSemEstoque.setQuantidadeAtual(new BigDecimal(0));
        produtoSemEstoque.setQuantidadeReservada(new BigDecimal(1));

        ItemOSProduto item = new ItemOSProduto();
        item.setQuantidade(new BigDecimal(1));
        item.setProduto(produtoSemEstoque);

        osMock.setItensProduto(Set.of(item));
        when(repository.findById(osId)).thenReturn(Optional.of(osMock));
        // 2. Act
        osService.aprovarOrcamento(osId);
        // 3. Assert
        verify(movimentacaoEstoqueService, times(1)).reservarItens(osMock);
        verify(repository).save(osCaptor.capture());
        assertEquals(StatusOS.AGUARDANDO_FORNECEDOR, osCaptor.getValue().getStatus());
    }

    @Test
    @DisplayName("Deve alterar status para AGUARDANDO_EXECUCAO e salvar quando todos os itens tiverem estoque")
    void deveMudarStatusQuandoTiverEstoque() {
        // 1. Arrange
        Long osId = 1L;
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();
        osMock.setStatus(StatusOS.AGUARDANDO_FORNECEDOR);

        Produto produtoComEstoque = new Produto();
        produtoComEstoque.setQuantidadeAtual(new BigDecimal(5));
        produtoComEstoque.setQuantidadeReservada(new BigDecimal(5));

        ItemOSProduto item = new ItemOSProduto();
        item.setProduto(produtoComEstoque);
        item.setQuantidade(new BigDecimal(5));
        osMock.setItensProduto(Set.of(item));
        when(repository.findById(osId)).thenReturn(Optional.of(osMock));
        // 2. Act
        osService.verificarDisponibilidadePecas(osId);
        // 3. Assert
        verify(repository, times(1)).save(osCaptor.capture());
        assertEquals(StatusOS.AGUARDANDO_EXECUCAO, osCaptor.getValue().getStatus());
    }

    @Test
    @DisplayName("Não deve alterar status nem salvar a OS quando ainda faltar estoque de algum item")
    void naoDeveMudarStatusQuandoFaltarEstoque() {
        // 1. Arrange
        Long osId = 1L;
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();
        osMock.setStatus(StatusOS.AGUARDANDO_FORNECEDOR);

        Produto produtoSemEstoque = new Produto();
        produtoSemEstoque.setQuantidadeAtual(new BigDecimal("0"));
        produtoSemEstoque.setQuantidadeReservada(new BigDecimal("2"));

        ItemOSProduto item = new ItemOSProduto();
        item.setProduto(produtoSemEstoque);
        osMock.setItensProduto(Set.of(item));
        when(repository.findById(osId)).thenReturn(Optional.of(osMock));
        // 2. Act
        osService.verificarDisponibilidadePecas(osId);
        // 3. Assert
        verify(repository, never()).save(any(OrdemServico.class));
        assertEquals(StatusOS.AGUARDANDO_FORNECEDOR, osMock.getStatus());
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException ao verificar peças de OS com status diferente de AGUARDANDO_FORNECEDOR")
    void deveLancarExceptionAoVerificarPecasComStatusInvalido() {
        // 1. Arrange
        Long osId = 1L;
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();
        osMock.setStatus(StatusOS.AGUARDANDO_EXECUCAO);
        when(repository.findById(osId)).thenReturn(Optional.of(osMock));
        // 2. Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> osService.verificarDisponibilidadePecas(osId)
        );
        assertEquals("Operação inválida para o status atual da OS: AGUARDANDO_EXECUCAO", exception.getMessage());
        verify(repository, never()).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve iniciar a execução da OS, consumir o estoque reservado e alterar status para EM_EXECUCAO")
    void deveIniciarExecucaoEAcionarBaixaDeEstoque() {
        // 1. Arrange
        Long osId = 1L;
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();
        osMock.setStatus(StatusOS.AGUARDANDO_EXECUCAO);
        when(repository.findById(osId)).thenReturn(Optional.of(osMock));
        // 2. Act
        osService.iniciarExecucaoOS(osId);
        // 3. Assert
        verify(movimentacaoEstoqueService, times(1)).consumirReservasParaExecucao(osMock);
        verify(repository, times(1)).save(osCaptor.capture());
        assertEquals(StatusOS.EM_EXECUCAO, osCaptor.getValue().getStatus());
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException ao tentar iniciar execução de uma OS com status inválido")
    void deveLancarExceptionAoIniciarExecucaoComStatusInvalido() {
        // 1. Arrange
        Long osId = 1L;
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();
        osMock.setStatus(StatusOS.AGUARDANDO_FORNECEDOR);

        when(repository.findById(osId)).thenReturn(Optional.of(osMock));
        // 2. Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> osService.iniciarExecucaoOS(osId)
        );
        assertEquals("Operação inválida para o status atual da OS: AGUARDANDO_FORNECEDOR", exception.getMessage());
        verify(movimentacaoEstoqueService, never()).consumirReservasParaExecucao(any());
        verify(repository, never()).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve finalizar a OS com sucesso quando todos os serviços estiverem concluídos")
    void deveFinalizarExecucaoComSucesso() {
        // 1. Arrange
        Long osId = 1L;
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();
        osMock.setStatus(StatusOS.EM_EXECUCAO);
        ItemOSServico itemConcluido = new ItemOSServico();
        itemConcluido.setStatusItem(StatusItemServico.CONCLUIDO);
        osMock.setItensServico(Set.of(itemConcluido));
        when(repository.findById(osId)).thenReturn(Optional.of(osMock));
        // 2. Act
        osService.finalizarExecucaoOS(osId);
        // 3. Assert
        verify(repository, times(1)).save(osCaptor.capture());
        OrdemServico entidadeCapturada = osCaptor.getValue();
        assertEquals(StatusOS.FINALIZADA, entidadeCapturada.getStatus());
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException ao finalizar OS com serviços pendentes")
    void deveLancarExceptionQuandoHouverServicoPendente() {
        // 1. Arrange
        Long osId = 1L;
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();
        osMock.setStatus(StatusOS.EM_EXECUCAO);
        ItemOSServico itemPendente = new ItemOSServico();
        itemPendente.setStatusItem(StatusItemServico.PENDENTE);
        osMock.setItensServico(Set.of(itemPendente));
        when(repository.findById(osId)).thenReturn(Optional.of(osMock));

        // 2. Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> osService.finalizarExecucaoOS(osId)
        );

        assertEquals("Não é possível finalizar a OS. Existem serviços pendentes ou em andamento.", exception.getMessage());
        verify(repository, never()).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException por falha de integridade se a OS não tiver serviços atrelados na finalização")
    void deveLancarExceptionPorFalhaDeIntegridadeSemServicos() {
        // 1. Arrange
        Long osId = 1L;
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();
        osMock.setStatus(StatusOS.EM_EXECUCAO);
        osMock.setItensServico(Collections.emptySet());
        when(repository.findById(osId)).thenReturn(Optional.of(osMock));
        // 2. Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> osService.finalizarExecucaoOS(osId)
        );
        assertEquals("Falha de integridade: A OS chegou na finalização sem itens de serviço atrelados.", exception.getMessage());
        verify(repository, never()).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve entregar o veículo, alterar status para ENTREGUE e preencher a data de saída")
    void deveEntregarVeiculoComSucesso() {
        // 1. Arrange
        Long osId = 1L;
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();
        osMock.setStatus(StatusOS.FINALIZADA);
        when(repository.findById(osId)).thenReturn(Optional.of(osMock));
        // 2. Act
        osService.entregarVeiculo(osId);
        // 3. Assert
        verify(repository, times(1)).save(osCaptor.capture());
        OrdemServico entidadeCapturada = osCaptor.getValue();
        assertEquals(StatusOS.ENTREGUE, entidadeCapturada.getStatus());
        assertNotNull(entidadeCapturada.getDataSaida());
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException ao tentar entregar veículo de uma OS que não está FINALIZADA")
    void deveLancarExceptionAoEntregarVeiculoComStatusInvalido() {
        // 1. Arrange
        Long osId = 1L;
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();
        osMock.setStatus(StatusOS.EM_EXECUCAO);
        when(repository.findById(osId)).thenReturn(Optional.of(osMock));
        // 2. Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> osService.entregarVeiculo(osId)
        );
        assertEquals("Operação inválida para o status atual da OS: EM_EXECUCAO", exception.getMessage());
        verify(repository, never()).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve recalcular totais de produtos, serviços e valor final aplicando o desconto")
    void deveRecalcularTotaisComSucesso() {
        // 1. Arrange
        Long osId = 1L;
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();
        osMock.setValorDesconto(new BigDecimal("50.00"));
        ItemOSServico servico1 = mock(ItemOSServico.class);
        when(servico1.getValorCobrado()).thenReturn(new BigDecimal("100.00"));
        ItemOSServico servico2 = mock(ItemOSServico.class);
        when(servico2.getValorCobrado()).thenReturn(new BigDecimal("200.00"));
        osMock.setItensServico(Set.of(servico1, servico2));
        ItemOSProduto produto1 = mock(ItemOSProduto.class);
        when(produto1.getValorTotal()).thenReturn(new BigDecimal("50.00"));
        ItemOSProduto produto2 = mock(ItemOSProduto.class);
        when(produto2.getValorTotal()).thenReturn(new BigDecimal("100.00"));
        osMock.setItensProduto(Set.of(produto1, produto2));
        when(repository.findById(osId)).thenReturn(Optional.of(osMock));
        // 2. Act
        osService.recalcularTotais(osId);
        // 3. Assert
        verify(repository, times(1)).save(osCaptor.capture());
        OrdemServico entidadeCapturada = osCaptor.getValue();
        assertEquals(new BigDecimal("150.00"), entidadeCapturada.getValorTotalProdutos());
        assertEquals(new BigDecimal("300.00"), entidadeCapturada.getValorTotalServicos());
        assertEquals(new BigDecimal("400.00"), entidadeCapturada.getValorTotalOS());
    }

    @Test
    @DisplayName("Deve buscar detalhes da OS por código e mapear corretamente para o DTO")
    void deveBuscarDetalhesPorCodigoComSucesso() {
        // 1. Arrange
        String codigoRastreio = "OS-12345678";

        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();
        osMock.setId(1L);
        osMock.setCodigoRastreio(codigoRastreio);
        osMock.setReclamacaoCliente("Barulho no motor");
        osMock.setLaudoTecnico("Correia dentada gasta");

        //Configurando Cliente e Veículo para evitar NullPointerException no DTO
        var cliente = new Cliente();
        cliente.setNome("João da Silva");
        osMock.setCliente(cliente);

        var veiculo = new Veiculo();
        veiculo.setPlaca("ABC-1234");
        osMock.setVeiculo(veiculo);

        //Configurando Produto
        var produto = new Produto();
        produto.setNome("Correia Dentada");
        var itemProduto = new ItemOSProduto();
        itemProduto.setId(10L);
        itemProduto.setProduto(produto);
        itemProduto.setQuantidade(new BigDecimal("1"));
        itemProduto.setValorUnitarioVenda(new BigDecimal("150.00"));
        osMock.setItensProduto(Set.of(itemProduto));

        // Configurando Serviços (Um com mecânico, outro sem)
        var servicoTipo = new Servico();
        servicoTipo.setDescricao("Troca de Correia");

        var mecanico = new Funcionario();
        mecanico.setNome("Carlos Mecânico");

        var itemComMecanico = new ItemOSServico();
        itemComMecanico.setId(20L);
        itemComMecanico.setServico(servicoTipo);
        itemComMecanico.setMecanico(mecanico); // Tem mecânico
        itemComMecanico.setValorCobrado(new BigDecimal("200.00"));
        itemComMecanico.setStatusItem(StatusItemServico.CONCLUIDO);

        var itemSemMecanico = new ItemOSServico();
        itemSemMecanico.setId(21L);
        itemSemMecanico.setServico(servicoTipo);
        // Não setamos o mecânico aqui para forçar a string "Não atribuído"
        itemSemMecanico.setValorCobrado(new BigDecimal("50.00"));
        itemSemMecanico.setStatusItem(StatusItemServico.PENDENTE);

        osMock.setItensServico(Set.of(itemComMecanico, itemSemMecanico));

        // Mock do repositório
        when(repository.buscarPorCodigoComDetalhes(codigoRastreio)).thenReturn(Optional.of(osMock));

        // 2. Act
        OrdemServicoDetalhadaResponseDto response = osService.buscarDetalhesPorCodigo(codigoRastreio);

        // 3. Assert
        assertNotNull(response);
        assertEquals(codigoRastreio, response.codigoRastreio());
        assertEquals("João da Silva", response.nomeCliente());
        assertEquals("ABC-1234", response.placaVeiculo());

        // Verifica mapeamento da lista de produtos
        assertEquals(1, response.produtos().size());
        assertEquals("Correia Dentada", response.produtos().get(0).nomeProduto());

        // Verifica mapeamento da lista de serviços e a lógica do ternário
        assertEquals(2, response.servicos().size());

        // Precisamos encontrar qual DTO corresponde a qual item pelas características
        boolean achouMecanicoAtribuido = response.servicos().stream()
                .anyMatch(s -> s.mecanico().equals("Carlos Mecânico"));
        boolean achouMecanicoNaoAtribuido = response.servicos().stream()
                .anyMatch(s -> s.mecanico().equals("Não atribuído"));

        assertTrue(achouMecanicoAtribuido, "Deveria ter mapeado o nome do mecânico");
        assertTrue(achouMecanicoNaoAtribuido, "Deveria ter aplicado o fallback 'Não atribuído'");
    }


}