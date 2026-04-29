package com.dinoco.oficina.service;

import com.dinoco.oficina.dto.LinkWhatsAppDto;
import com.dinoco.oficina.dto.OrdemServicoResponseDto;
import com.dinoco.oficina.entity.ItemOSProduto;
import com.dinoco.oficina.entity.ItemOSServico;
import com.dinoco.oficina.entity.OrdemServico;
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
import java.util.List;
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
    private VeiculoService veiculoService;

    @Captor
    private ArgumentCaptor<OrdemServico> osCaptor;

    @Test
    void deveAbrirOrdemServicoQuandoDadosForemValidos() {
        // 1. Arrange
        var requestDto = OrdemServicoRequestDtoBuilder.umRequest().build();

        var clienteMock = ClienteBuilder.umCliente();
        var veiculoMock = VeiculoBuilder.umVeiculo();
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

        // Validações fortes (Garante que a regra de negócio aplicou os dados do request na entidade)
        assertEquals(clienteMock, entidadeCapturada.getCliente());
        assertEquals(veiculoMock, entidadeCapturada.getVeiculo());
        assertEquals(requestDto.quilometragemEntrada(), entidadeCapturada.getQuilometragemEntrada());
        assertEquals(requestDto.reclamacaoCliente(), entidadeCapturada.getReclamacaoCliente());

        // Validações de estado inicial (Business Rules)
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
        // Captura a entidade que foi enviada para o save()
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

        // Status que NÃO permite iniciar diagnóstico
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
        // Simula a OS em um status que NÃO permite a conclusão do diagnóstico
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

        // Garante que a lista de itens está vazia
        osMock.setItensServico(Collections.EMPTY_SET);

        when(repository.findById(osId)).thenReturn(Optional.of(osMock));

        // 2. Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> osService.concluirDiagnostico(osId, laudoTecnico)
        );

        // Valida a mensagem exata
        assertEquals("Para concluir o diagnóstico, a OS deve possuir ao menos um item de serviço.", exception.getMessage());

        // Garante a interrupção do fluxo
        verify(repository, never()).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve enviar orçamento, alterar status para AGUARDANDO_APROVACAO e retornar link do WhatsApp")
    void deveEnviarOrcamentoEGerarLinkWhatsAppComSucesso() {
        // 1. Arrange
        Long osId = 1L;
        OrdemServico osMock = OrdemServicoBuilder.umaOrdemServico().build();

        // Configuramos o Cliente para o teste do WhatsApp
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
        // Decodificamos a URL para validar o conteúdo de forma legível
        String urlDecodificada = URLDecoder.decode(urlGerada, StandardCharsets.UTF_8);

        // 1. Verifica se o telefone foi limpo e formatado corretamente
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

        // Item de serviço inválido (R$ 0,00)
        ItemOSServico servicoInvalido = new ItemOSServico();
        servicoInvalido.setValorCobrado(BigDecimal.ZERO);
        osMock.setItensServico(Set.of(servicoInvalido));

        // Item de produto válido
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

        // Item de serviço válido
        ItemOSServico servicoValido = new ItemOSServico();
        servicoValido.setValorCobrado(new BigDecimal("500.00"));
        osMock.setItensServico(Set.of(servicoValido));

        // Item de produto inválido (R$ 0,00)
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


}