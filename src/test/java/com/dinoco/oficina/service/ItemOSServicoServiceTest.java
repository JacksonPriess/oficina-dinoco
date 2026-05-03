package com.dinoco.oficina.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import com.dinoco.oficina.entity.Funcionario;
import com.dinoco.oficina.entity.ItemOSServico;
import com.dinoco.oficina.entity.OrdemServico;
import com.dinoco.oficina.entity.Servico;
import com.dinoco.oficina.enums.StatusItemServico;
import com.dinoco.oficina.enums.StatusOS;
import com.dinoco.oficina.repository.ItemOSServicoRepository;
import com.dinoco.oficina.util.builders.FuncionarioBuilder;
import com.dinoco.oficina.util.builders.ItemOSDtoBuilders;
import com.dinoco.oficina.util.builders.OrdemServicoBuilder;
import com.dinoco.oficina.util.builders.ServicoBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ItemOSServicoServiceTest {

    @Mock
    private ItemOSServicoRepository repository;
    @Mock
    private OrdemServicoService ordemServicoService;
    @Mock
    private ServicoService servicoService;
    @Mock
    private FuncionarioService funcionarioService;

    @InjectMocks
    private ItemOSServicoService service;

    @Captor
    private ArgumentCaptor<ItemOSServico> itemCaptor;

    @Test
    @DisplayName("Deve adicionar item de serviço com sucesso")
    void deveAdicionarItemServicoComSucesso() {
        Long osId = 1L;
        var dto = ItemOSDtoBuilders.adicionarServicoDto(1L, 2L);

        OrdemServico os = OrdemServicoBuilder.umaOrdemServico().build();
        os.setId(osId);
        os.setStatus(StatusOS.EM_DIAGNOSTICO);
        os.setItensServico(new HashSet<>());

        Servico servico = ServicoBuilder.umServico().comId(1L).comPrecoPadrao(new BigDecimal("100.00")).build();
        Funcionario mecanico = FuncionarioBuilder.umFuncionario().comId(2L).build();

        when(ordemServicoService.buscarOuFalhar(osId)).thenReturn(os);
        when(repository.existsByOrdemServicoIdAndServicoId(osId, dto.servicoId())).thenReturn(false);
        when(servicoService.buscarEntidadePorId(dto.servicoId())).thenReturn(servico);
        when(funcionarioService.buscarEntidadePorId(dto.mecanicoId())).thenReturn(mecanico);

        service.adicionarItemServico(osId, dto);

        verify(repository).save(itemCaptor.capture());
        ItemOSServico itemSalvo = itemCaptor.getValue();

        assertThat(itemSalvo.getServico()).isEqualTo(servico);
        assertThat(itemSalvo.getMecanico()).isEqualTo(mecanico);
        assertThat(itemSalvo.getValorCobrado()).isEqualTo(new BigDecimal("100.00"));
        assertThat(itemSalvo.getStatusItem()).isEqualTo(StatusItemServico.PENDENTE);
        assertThat(os.getItensServico()).hasSize(1);

        verify(ordemServicoService).recalcularTotais(osId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar serviço em OS que não está em diagnóstico")
    void deveLancarExcecaoAoAdicionarEmOsForaDiagnostico() {
        Long osId = 1L;
        var dto = ItemOSDtoBuilders.adicionarServicoDto(1L, null);

        OrdemServico os = OrdemServicoBuilder.umaOrdemServico().build();
        os.setStatus(StatusOS.AGUARDANDO_ORCAMENTO); // Status inválido para adição inicial

        when(ordemServicoService.buscarOuFalhar(osId)).thenReturn(os);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.adicionarItemServico(osId, dto));

        assertThat(exception.getMessage()).isEqualTo("Inicie o diagnóstico da OS antes de adicionar itens de serviço.");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve alterar item de serviço com sucesso")
    void deveAlterarItemServicoComSucesso() {
        Long itemId = 1L;
        var dto = ItemOSDtoBuilders.alterarServicoDto(new BigDecimal("200.00"), null);

        OrdemServico os = OrdemServicoBuilder.umaOrdemServico().build();
        os.setId(99L);
        os.setStatus(StatusOS.AGUARDANDO_ORCAMENTO);

        ItemOSServico item = new ItemOSServico();
        item.setId(itemId);
        item.setOrdemServico(os);
        item.setValorCobrado(new BigDecimal("150.00"));

        when(repository.findById(itemId)).thenReturn(Optional.of(item));

        service.alterarItemServico(itemId, dto);

        verify(repository).save(itemCaptor.capture());
        assertThat(itemCaptor.getValue().getValorCobrado()).isEqualTo(new BigDecimal("200.00"));
        verify(ordemServicoService).recalcularTotais(os.getId());
    }

    @Test
    @DisplayName("Deve iniciar execução do item de serviço")
    void deveIniciarExecucao() {
        Long itemId = 1L;
        ItemOSServico item = new ItemOSServico();
        item.setStatusItem(StatusItemServico.PENDENTE);

        when(repository.findById(itemId)).thenReturn(Optional.of(item));

        service.iniciarExecucaoItemServico(itemId);

        verify(repository).save(itemCaptor.capture());
        assertThat(itemCaptor.getValue().getStatusItem()).isEqualTo(StatusItemServico.EM_ANDAMENTO);
        assertThat(itemCaptor.getValue().getDataInicio()).isNotNull();
    }
}