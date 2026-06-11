package com.dinoco.oficina.service;
/*
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.Optional;
import com.dinoco.oficina.catalogoservico.infrastructure.web.dto.ServicoRequestDto;
import com.dinoco.oficina.catalogoservico.infrastructure.web.dto.ServicoResponseDto;
import com.dinoco.oficina.entity.Servico;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.repository.ServicoRepository;
//import com.dinoco.oficina.util.builders.ServicoBuilder;
//import com.dinoco.oficina.util.builders.ServicoRequestDtoBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServicoServiceTest {

    @Mock
    private ServicoRepository repository;

    @InjectMocks
    private ServicoService service;

    @Captor
    private ArgumentCaptor<Servico> servicoCaptor;

    @Test
    @DisplayName("Deve criar serviço com sucesso")
    void deveCriarServicoComSucesso() {
        var request = ServicoRequestDtoBuilder.umRequest();
        var servicoMock = ServicoBuilder.umServico().build();

        when(repository.existsByDescricaoIgnoreCase(request.descricao())).thenReturn(false);
        when(repository.save(any(Servico.class))).thenReturn(servicoMock);

        ServicoResponseDto response = service.criar(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.descricao()).isEqualTo("Troca de Óleo");

        verify(repository).save(servicoCaptor.capture());
        Servico salvo = servicoCaptor.getValue();

        assertThat(salvo.getDescricao()).isEqualTo(request.descricao());
        assertThat(salvo.getPrecoPadrao()).isEqualTo(request.precoPadrao());
        assertThat(salvo.getTempoEstimadoMinutos()).isEqualTo(request.tempoEstimadoMinutos());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar serviço com descrição já existente")
    void deveLancarExcecaoAoCriarComDescricaoExistente() {
        var request = ServicoRequestDtoBuilder.umRequest();

        when(repository.existsByDescricaoIgnoreCase(request.descricao())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.criar(request));

        assertEquals("Já existe um serviço cadastrado com este nome.", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar ServicoResponseDto por ID com sucesso")
    void deveBuscarPorIdComSucesso() {
        Long id = 1L;
        Servico servico = ServicoBuilder.umServico().comId(id).build();

        when(repository.findById(id)).thenReturn(Optional.of(servico));

        ServicoResponseDto response = service.buscarPorId(id);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(id);
    }

    @Test
    @DisplayName("Deve buscar entidade Servico por ID com sucesso")
    void deveBuscarEntidadePorIdComSucesso() {
        Long id = 1L;
        Servico servico = ServicoBuilder.umServico().comId(id).build();

        when(repository.findById(id)).thenReturn(Optional.of(servico));

        Servico resultado = service.buscarEntidadePorId(id);

        assertThat(resultado.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar Serviço por ID inexistente")
    void deveLancarExcecaoAoBuscarPorIdInexistente() {
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(id));
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarEntidadePorId(id));
    }

    @Test
    @DisplayName("Deve atualizar serviço quando a descrição for alterada e for inédita")
    void deveAtualizarQuandoDescricaoMudaEInedita() {
        Long id = 1L;
        Servico servicoExistente = ServicoBuilder.umServico().comDescricao("Antiga Descrição").build();
        var request = ServicoRequestDtoBuilder.umRequestComNovaDescricao("Nova Descrição");

        when(repository.findById(id)).thenReturn(Optional.of(servicoExistente));
        // Simula que a NOVA descrição ainda não existe no banco
        when(repository.existsByDescricaoIgnoreCase(request.descricao())).thenReturn(false);
        when(repository.save(any(Servico.class))).thenReturn(servicoExistente);

        service.atualizar(id, request);

        verify(repository).save(servicoCaptor.capture());
        Servico atualizado = servicoCaptor.getValue();

        assertThat(atualizado.getDescricao()).isEqualTo("Nova Descrição");
        assertThat(atualizado.getPrecoPadrao()).isEqualTo(new BigDecimal("150.00"));
    }

    @Test
    @DisplayName("Deve atualizar serviço normalmente quando a descrição NÃO for alterada")
    void deveAtualizarQuandoDescricaoPermaneceAMesma() {
        Long id = 1L;
        String descricaoUnica = "Troca de Óleo";

        Servico servicoExistente = ServicoBuilder.umServico().comDescricao(descricaoUnica).build();

        // Request vem com o MESMO nome, mas muda o preço
        var request = new ServicoRequestDto(descricaoUnica, new BigDecimal("300.00"), 45);

        when(repository.findById(id)).thenReturn(Optional.of(servicoExistente));
        when(repository.save(any(Servico.class))).thenReturn(servicoExistente);

        service.atualizar(id, request);

        // A grande sacada: como a descrição é igual ao que já estava no banco,
        // ele nem deve ir consultar o banco para ver se existe!
        verify(repository, never()).existsByDescricaoIgnoreCase(anyString());

        verify(repository).save(servicoCaptor.capture());
        assertThat(servicoCaptor.getValue().getPrecoPadrao()).isEqualTo(new BigDecimal("300.00"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar serviço para uma descrição que já pertence a outro")
    void deveLancarExcecaoAoAtualizarParaDescricaoJaExistente() {
        Long id = 1L;
        Servico servicoExistente = ServicoBuilder.umServico().comDescricao("Serviço A").build();

        // Tenta mudar o nome para "Serviço B"
        var request = ServicoRequestDtoBuilder.umRequestComNovaDescricao("Serviço B");

        when(repository.findById(id)).thenReturn(Optional.of(servicoExistente));
        // Retorna true, dizendo que "Serviço B" já existe
        when(repository.existsByDescricaoIgnoreCase(request.descricao())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.atualizar(id, request));

        assertEquals("Já existe outro serviço cadastrado com esta descrição.", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve inativar serviço com sucesso")
    void deveDesativarServico() {
        Long id = 1L;
        Servico servico = ServicoBuilder.umServico().build();
        servico.setAtivo(true);

        when(repository.findById(id)).thenReturn(Optional.of(servico));

        service.desativar(id);

        verify(repository).save(servicoCaptor.capture());
        assertThat(servicoCaptor.getValue().getAtivo()).isFalse();
    }


}

 */
