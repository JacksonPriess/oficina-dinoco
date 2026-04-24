package com.dinoco.oficina.service;

import com.dinoco.oficina.dto.OrdemServicoRequestDto;
import com.dinoco.oficina.dto.OrdemServicoResponseDto;
import com.dinoco.oficina.entity.Cliente;
import com.dinoco.oficina.entity.OrdemServico;
import com.dinoco.oficina.entity.Veiculo;
import com.dinoco.oficina.enums.StatusOS;
import com.dinoco.oficina.repository.OrdemServicoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdemServicoServiceTest {

    @InjectMocks
    private OrdemServicoService osService;

    @Mock
    private OrdemServicoRepository osRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private VeiculoService veiculoService;

    @Test
    @DisplayName("Abrir OS com sucesso")
    void deveAbrirOsComSucesso() {
        // 1. Arrange (Preparação)
        Long clienteId = 1L;
        Long veiculoId = 1L;

        OrdemServicoRequestDto requestDto = new OrdemServicoRequestDto(
                clienteId, veiculoId, 85000, "Barulho no motor"
        );

        var clienteMock = new Cliente();
        var veiculoMock = new Veiculo();
        var osSalvaMock = new OrdemServico(clienteMock, veiculoMock, 85000, "Barulho no motor");
        osSalvaMock.setId(100L); // Simulando o ID gerado pelo banco

        // Ensinando os Mocks como eles devem se comportar
        when(osRepository.existeOsAtivaParaVeiculo(veiculoId)).thenReturn(false);
        when(clienteService.buscarEntidadePorId(clienteId)).thenReturn(clienteMock);
        when(veiculoService.buscarEntidadePorId(veiculoId)).thenReturn(veiculoMock);
        when(osRepository.save(any(OrdemServico.class))).thenReturn(osSalvaMock);

        // 2. Act (Ação)
        OrdemServicoResponseDto response = osService.abrirOs(requestDto);

        // 3. Assert (Verificação)
        assertNotNull(response);
        // Verificamos se o Service realmente chamou o save() do Repository exatamente 1 vez
        verify(osRepository, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Abrir OS com erro")
    void deveLancarExcecaoQuandoVeiculoJaPossuiOsAtiva() {
        // 1. Arrange
        Long veiculoId = 1L;
        OrdemServicoRequestDto requestDto = new OrdemServicoRequestDto(
                1L, veiculoId, 85000, "Barulho no motor"
        );

        when(osRepository.existeOsAtivaParaVeiculo(veiculoId)).thenReturn(true);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            osService.abrirOs(requestDto);
        });
        assertEquals("Já existe uma Ordem de Serviço aberta para este veículo.", exception.getMessage());
        verify(osRepository, never()).save(any());
        verifyNoInteractions(clienteService);
        verifyNoInteractions(veiculoService);
    }

    @Test
    @DisplayName("Iniciar diagnóstico com sucesso")
    void deveIniciarDiagnosticoComSucesso(){
        Long osId = 100L;
        var clienteMock = new Cliente();
        var veiculoMock = new Veiculo();
        var ordemServico = new OrdemServico(clienteMock, veiculoMock, 85000, "Barulho no motor");
        ordemServico.setId(osId);
        when(osRepository.findById(osId)).thenReturn(Optional.of(ordemServico));
        when(osRepository.save(any(OrdemServico.class))).thenReturn(ordemServico);
        osService.iniciarDiagnostico(osId);
        assertEquals(StatusOS.EM_DIAGNOSTICO, ordemServico.getStatus());
        verify(osRepository, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Iniciar diagnóstico com erro")
    void deveLancarExcecaoQuandoOsNaoEstiverRecebida(){
        Long osId = 100L;
        var ordemServico = new OrdemServico(new Cliente(), new Veiculo(), 85000, "Barulho");
        ordemServico.setId(osId);
        ordemServico.setStatus(StatusOS.AGUARDANDO_ORCAMENTO);
        when(osRepository.findById(osId)).thenReturn(Optional.of(ordemServico));
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            osService.iniciarDiagnostico(osId);
        });
        assertTrue(exception.getMessage().contains("Para iniciar um diagnóstico a OS deve estar com status RECEBIDA"));
        verify(osRepository, never()).save(any());
    }

    @Test
    @DisplayName("Concluir diagnóstico com sucesso")
    void deveConcluirDiagnosticoComSucesso(){
        Long osId = 100L;
        var clienteMock = new Cliente();
        var veiculoMock = new Veiculo();
        var ordemServico = new OrdemServico(clienteMock, veiculoMock, 85000, "Barulho no motor");
        ordemServico.setId(osId);
        ordemServico.setStatus(StatusOS.EM_DIAGNOSTICO);
        when(osRepository.findById(osId)).thenReturn(Optional.of(ordemServico));
        when(osRepository.save(any(OrdemServico.class))).thenReturn(ordemServico);
        osService.concluirDiagnostico(osId, "Laudo concluído");
        assertEquals(StatusOS.AGUARDANDO_ORCAMENTO, ordemServico.getStatus());
        assertEquals("Laudo concluído", ordemServico.getLaudoTecnico());
        verify(osRepository, times(1)).save(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Concluir diagnóstico com erro")
    void deveLancarExcecaoQuandoConcluirDiagnosticoEOSNaoEstiverEmDiagnostico(){
        Long osId = 100L;
        var ordemServico = new OrdemServico(new Cliente(), new Veiculo(), 85000, "Barulho");
        ordemServico.setId(osId);
        when(osRepository.findById(osId)).thenReturn(Optional.of(ordemServico));
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            osService.concluirDiagnostico(osId, "Laudo concluído");
        });
        assertTrue(exception.getMessage().contains("Operação inválida para o status atual da OS: RECEBIDA"));
        verify(osRepository, never()).save(any());
    }

    /**
     *
     * concluirDiagnostico() -> alterar para AGUARDANDO_ORCAMENTO    ... obs precisa ter ao menos um item na os.
     * enviarOrcamento() -> alterar para AGUARDANDO_APROVACAO        ... valor da os precisa ser maior que zero
     * reprovarOrcamento() -> alterar para REPROVAR ... precisa informar data de da OS.
     * aprovarOrcamento() ->  Logica mais complexa :
     *  1° passo : Sistema vai reservar a peça no estoque para todos os itens da OS.
     *  2° passo : Sistema verificará se existe quantidade real, ( getQuantidadeDisponivel > 0 ) para todos os itens de produto.
     *         i - Se tiver saldo para todos os itens de produto, alterar status para AGUARDANDO_EXECUCAO.
     *         ii - Se não, altera o status para AGUARDANDO_FORNECEDOR. e aqui o atendente precisa agir, e vai fazer movimento de entrada da peca no estoque
     *  refreshNaOS -> aqui apenas será possível clicar, quando a OS estiver em AGUARDANDO_FORNECEDOR, pois o sistema vai calcular novamente apenas os itens ( getQuantidadeDisponivel > 0 ), e se a peça nova chegou no estoque, alterar status para AGUARDANDO_EXECUCAO
     *
     *  iniciarExecucaoOS() ->   Alterar status da OS para EM_EXECUCAO, precisa atualizar o estoque para dar baixa ba peca.
     *  iniciarExecucaoItemServicoOS() -> Alterar status do item de servico para EM_ANDAMENTO
     *  concluirItemServicoOS -> Alterar status do item de servico para CONCLUIDO
     *
     *  finalizarExecucaoOS() ->  Alterar status da OS para FINALIZADA, aqui quer dizer que todos os servicos foram realizados e o carro está pronto.
     *  concluirOS -> Alterar status da OS para ENTREGUE
     */
}