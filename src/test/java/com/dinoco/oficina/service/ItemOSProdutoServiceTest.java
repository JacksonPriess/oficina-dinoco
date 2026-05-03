package com.dinoco.oficina.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import com.dinoco.oficina.entity.ItemOSProduto;
import com.dinoco.oficina.entity.OrdemServico;
import com.dinoco.oficina.entity.Produto;
import com.dinoco.oficina.enums.StatusOS;
import com.dinoco.oficina.repository.ItemOSProdutoRepository;
import com.dinoco.oficina.util.builders.ItemOSDtoBuilders;
import com.dinoco.oficina.util.builders.OrdemServicoBuilder;
import com.dinoco.oficina.util.builders.ProdutoBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ItemOSProdutoServiceTest {

    @Mock
    private ItemOSProdutoRepository repository;
    @Mock
    private OrdemServicoService ordemServicoService;
    @Mock
    private ProdutoService produtoService;

    @InjectMocks
    private ItemOSProdutoService service;

    @Captor
    private ArgumentCaptor<ItemOSProduto> itemCaptor;

    @Test
    @DisplayName("Deve adicionar item de produto com sucesso e calcular valor total")
    void deveAdicionarItemProdutoComSucesso() {
        Long osId = 1L;
        var dto = ItemOSDtoBuilders.adicionarProdutoDto(1L, new BigDecimal(2)); // Quantidade 2

        OrdemServico os = OrdemServicoBuilder.umaOrdemServico().build();
        os.setId(osId);
        os.setStatus(StatusOS.EM_DIAGNOSTICO);
        os.setItensProduto(new HashSet<>());

        Produto produto = ProdutoBuilder.umProduto().comPrecoVenda(new BigDecimal("50.00")).build();

        when(ordemServicoService.buscarOuFalhar(osId)).thenReturn(os);
        when(repository.existsByOrdemServicoIdAndProdutoId(osId, dto.produtoId())).thenReturn(false);
        when(produtoService.buscarEntidadePorId(dto.produtoId())).thenReturn(produto);
        service.adicionarItemProduto(osId, dto);
        verify(repository).save(itemCaptor.capture());
        ItemOSProduto itemSalvo = itemCaptor.getValue();
        assertThat(itemSalvo.getProduto()).isEqualTo(produto);
        assertThat(itemSalvo.getQuantidade()).isEqualTo(new BigDecimal(2));
        assertThat(itemSalvo.getValorUnitarioVenda()).isEqualTo(new BigDecimal("50.00"));
        assertThat(itemSalvo.getValorTotal()).isEqualTo(new BigDecimal("100.00")); // 2 * 50
        verify(ordemServicoService).recalcularTotais(osId);
    }

    @Test
    @DisplayName("Deve lançar exceção se produto já estiver na OS")
    void deveLancarExcecaoSeProdutoJaAdicionado() {
        Long osId = 1L;
        var dto = ItemOSDtoBuilders.adicionarProdutoDto(1L, new BigDecimal(1));

        OrdemServico os = OrdemServicoBuilder.umaOrdemServico().build();
        os.setStatus(StatusOS.EM_DIAGNOSTICO);

        when(ordemServicoService.buscarOuFalhar(osId)).thenReturn(os);
        when(repository.existsByOrdemServicoIdAndProdutoId(osId, dto.produtoId())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.adicionarItemProduto(osId, dto));

        assertThat(exception.getMessage()).isEqualTo("Este produto já foi adicionado a esta Ordem de Serviço.");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve alterar item de produto recalculando total com sucesso")
    void deveAlterarItemProdutoComSucesso() {
        Long itemId = 1L;
        var dto = ItemOSDtoBuilders.alterarProdutoDto(new BigDecimal(3), new BigDecimal("40.00"));

        OrdemServico os = OrdemServicoBuilder.umaOrdemServico().build();
        os.setId(99L);
        os.setStatus(StatusOS.AGUARDANDO_ORCAMENTO);

        Produto produto = ProdutoBuilder.umProduto().comPrecoVenda(new BigDecimal("50.00")).build();

        ItemOSProduto item = new ItemOSProduto();
        item.setId(itemId);
        item.setOrdemServico(os);
        item.setProduto(produto);

        when(repository.findById(itemId)).thenReturn(Optional.of(item));

        service.alterarItemProduto(itemId, dto);

        verify(repository).save(itemCaptor.capture());
        ItemOSProduto alterado = itemCaptor.getValue();

        assertThat(alterado.getQuantidade()).isEqualTo(new BigDecimal(3));
        assertThat(alterado.getValorUnitarioVenda()).isEqualTo(new BigDecimal("40.00"));
        assertThat(alterado.getValorTotal()).isEqualTo(new BigDecimal("120.00")); // 3 * 40

        verify(ordemServicoService).recalcularTotais(os.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alterar o item fora de status permitidos")
    void deveLancarExcecaoAlterarStatusInvalido() {
        Long itemId = 1L;
        var dto = ItemOSDtoBuilders.alterarProdutoDto(new BigDecimal(1), new BigDecimal("10.00"));

        OrdemServico os = OrdemServicoBuilder.umaOrdemServico().build();
        os.setStatus(StatusOS.FINALIZADA);

        ItemOSProduto item = new ItemOSProduto();
        item.setOrdemServico(os);

        when(repository.findById(itemId)).thenReturn(Optional.of(item));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.alterarItemProduto(itemId, dto));

        assertThat(exception.getMessage()).contains("A OS não permite modificação em itens");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve remover item de produto com sucesso")
    void deveRemoverItemProdutoComSucesso() {
        Long itemId = 1L;

        OrdemServico os = OrdemServicoBuilder.umaOrdemServico().build();
        os.setId(99L);
        os.setStatus(StatusOS.EM_DIAGNOSTICO);
        os.setItensProduto(new HashSet<>());

        ItemOSProduto item = new ItemOSProduto();
        item.setId(itemId);
        item.setOrdemServico(os);
        os.getItensProduto().add(item);

        when(repository.findById(itemId)).thenReturn(Optional.of(item));

        service.removerItemProduto(itemId);

        verify(repository).delete(item);
        assertThat(os.getItensProduto()).isEmpty();
        verify(ordemServicoService).recalcularTotais(os.getId());
    }
}