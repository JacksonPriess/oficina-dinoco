package com.dinoco.oficina.cliente.infrastructure.web;

import com.dinoco.oficina.cliente.infrastructure.config.ClienteConfig;
import com.dinoco.oficina.cliente.infrastructure.gateways.*;
import com.dinoco.oficina.cliente.infrastructure.persistence.*;
import com.dinoco.oficina.cliente.infrastructure.web.mapper.ClienteWebMapper;
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
class ClienteHttpMappingTest {
    private final ClienteJpaRepository repository = mock(ClienteJpaRepository.class);
    private ClienteEntity persisted;
    private MockMvc mvc;
    private AnnotationConfigApplicationContext context;
    private static final String BODY = """
            {"tipoPessoa":"F","documento":"52998224725","nome":"Cliente teste","email":"cliente@example.com","telefone":"47999999999","enderecos":[{"cep":"89200000","logradouro":"Rua A","numero":"12","complemento":"Casa","bairro":"Centro","cidade":"Joinville","uf":"SC"}]}
            """;

    @BeforeEach
    void configurar() {
        context = new AnnotationConfigApplicationContext();
        context.register(ClienteConfig.class, ClienteCommandGatewayImpl.class, ClienteQueryGatewayImpl.class, ClienteSpringWebController.class);
        context.registerBean(ClienteJpaRepository.class, () -> repository);
        context.registerBean(ClienteWebMapper.class, () -> Mappers.getMapper(ClienteWebMapper.class));
        
        context.refresh();
        mvc = MockMvcBuilders.standaloneSetup(context.getBean(ClienteSpringWebController.class))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        when(repository.save(any(ClienteEntity.class))).thenAnswer(invocation -> {
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
        mvc.perform(post("/api/clientes").contentType("application/json").content(BODY))
                .andExpect(status().isCreated()).andExpect(header().string("Location", "http://localhost/api/clientes/17"))
                .andExpect(jsonPath("$.id").value(17)).andExpect(jsonPath("$.nome").value("Cliente teste"));
        mvc.perform(get("/api/clientes/17")).andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Cliente teste"));
        mvc.perform(put("/api/clientes/17").contentType("application/json").content(BODY.replace("Cliente teste", "Cliente atualizado")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Cliente atualizado"));
        mvc.perform(delete("/api/clientes/17")).andExpect(status().isNoContent());
        mvc.perform(get("/api/clientes/17")).andExpect(status().isOk()).andExpect(jsonPath("$.ativo").value(false));
        verify(repository, times(3)).save(any(ClienteEntity.class));
    }

    @Test
    void rejeitarCorpoInvalidoSemPersistir() throws Exception {
        mvc.perform(post("/api/clientes").contentType("application/json").content("{}")).andExpect(status().isBadRequest());
        verify(repository, never()).save(any());
    }

    @Test
    void consultaAusenteNaoPodeRetornarSucesso() throws Exception {
        mvc.perform(get("/api/clientes/999")).andExpect(status().is4xxClientError());
        verify(repository).findById(999L);
        verify(repository, never()).save(any());
    }
}
