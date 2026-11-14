package com.dinoco.oficina.funcionario.infrastructure.web;

import com.dinoco.oficina.funcionario.infrastructure.config.FuncionarioConfig;
import com.dinoco.oficina.funcionario.infrastructure.gateways.*;
import com.dinoco.oficina.funcionario.infrastructure.persistence.*;
import com.dinoco.oficina.funcionario.infrastructure.web.mapper.FuncionarioWebMapper;
import com.dinoco.oficina.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.*;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Verifica o contrato HTTP, mapeamentos e casos de uso com a fronteira JPA simulada. */
class FuncionarioHttpMappingTest {
    private final FuncionarioJpaRepository repository = mock(FuncionarioJpaRepository.class);
    private FuncionarioEntity persisted;
    private MockMvc mvc;
    private AnnotationConfigApplicationContext context;
    private static final String BODY = """
            {"nome":"Mecanico teste","cpf":"52998224725","cargo":"MECANICO","criarAcesso":false}
            """;

    @BeforeEach
    void configurar() {
        context = new AnnotationConfigApplicationContext();
        context.register(FuncionarioConfig.class, FuncionarioCommandGatewayImpl.class, FuncionarioQueryGatewayImpl.class, FuncionarioSpringWebController.class);
        context.registerBean(FuncionarioJpaRepository.class, () -> repository);
        context.registerBean(FuncionarioWebMapper.class, () -> Mappers.getMapper(FuncionarioWebMapper.class));
        context.registerBean(com.dinoco.oficina.funcionario.application.gateways.UsuarioSistemaGateway.class, () -> mock(com.dinoco.oficina.funcionario.application.gateways.UsuarioSistemaGateway.class));
        context.refresh();
        mvc = MockMvcBuilders.standaloneSetup(context.getBean(FuncionarioSpringWebController.class))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        when(repository.save(any(FuncionarioEntity.class))).thenAnswer(invocation -> {
            persisted = invocation.getArgument(0);
            persisted.setId(17L);
            return persisted;
        });
        when(repository.findById(17L)).thenAnswer(invocation -> Optional.ofNullable(persisted));
    }

    @AfterEach
    void fechar() { context.close(); }

    @Test
    void criarConsultarAtualizarEDesativarPreservandoCampos() throws Exception {
        mvc.perform(post("/api/funcionarios").contentType("application/json").content(BODY))
                .andExpect(status().isCreated()).andExpect(header().string("Location", "http://localhost/api/funcionarios/17"))
                .andExpect(jsonPath("$.id").value(17)).andExpect(jsonPath("$.nome").value("Mecanico teste"));
        mvc.perform(get("/api/funcionarios/17")).andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Mecanico teste"));
        mvc.perform(put("/api/funcionarios/17").contentType("application/json").content(BODY.replace("Mecanico teste", "Mecanico atualizado")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Mecanico atualizado"));
        mvc.perform(delete("/api/funcionarios/17")).andExpect(status().isNoContent());
        mvc.perform(get("/api/funcionarios/17")).andExpect(status().isOk()).andExpect(jsonPath("$.ativo").value(false));
        verify(repository, times(3)).save(any(FuncionarioEntity.class));
    }

    @Test
    void rejeitarCorpoInvalidoSemPersistir() throws Exception {
        mvc.perform(post("/api/funcionarios").contentType("application/json").content("{}")).andExpect(status().isBadRequest());
        verify(repository, never()).save(any());
    }

    @Test
    void consultaAusenteNaoPodeRetornarSucesso() throws Exception {
        mvc.perform(get("/api/funcionarios/999")).andExpect(status().is4xxClientError());
        verify(repository).findById(999L);
        verify(repository, never()).save(any());
    }
}
