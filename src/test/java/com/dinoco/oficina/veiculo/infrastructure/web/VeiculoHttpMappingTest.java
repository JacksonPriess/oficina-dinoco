package com.dinoco.oficina.veiculo.infrastructure.web;

import com.dinoco.oficina.veiculo.infrastructure.config.VeiculoConfig;
import com.dinoco.oficina.veiculo.infrastructure.gateways.*;
import com.dinoco.oficina.veiculo.infrastructure.persistence.*;
import com.dinoco.oficina.veiculo.infrastructure.web.mapper.VeiculoWebMapper;
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
class VeiculoHttpMappingTest {
    private final VeiculoJpaRepository repository = mock(VeiculoJpaRepository.class);
    private VeiculoEntity persisted;
    private MockMvc mvc;
    private AnnotationConfigApplicationContext context;
    private static final String BODY = """
            {"placa":"ABC1D23","marca":"Fiat","modelo":"Uno","anoFabricacao":2020,"anoModelo":2021,"cor":"Branco","chassi":"12345678901234567","motor":"1.0"}
            """;

    @BeforeEach
    void configurar() {
        context = new AnnotationConfigApplicationContext();
        context.register(VeiculoConfig.class, VeiculoCommandGatewayImpl.class, VeiculoQueryGatewayImpl.class, VeiculoSpringWebController.class);
        context.registerBean(VeiculoJpaRepository.class, () -> repository);
        context.registerBean(VeiculoWebMapper.class, () -> Mappers.getMapper(VeiculoWebMapper.class));
        
        context.refresh();
        mvc = MockMvcBuilders.standaloneSetup(context.getBean(VeiculoSpringWebController.class))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        when(repository.save(any(VeiculoEntity.class))).thenAnswer(invocation -> {
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
        mvc.perform(post("/api/veiculos").contentType("application/json").content(BODY))
                .andExpect(status().isCreated()).andExpect(header().string("Location", "http://localhost/api/veiculos/17"))
                .andExpect(jsonPath("$.id").value(17)).andExpect(jsonPath("$.cor").value("Branco"));
        mvc.perform(get("/api/veiculos/17")).andExpect(status().isOk()).andExpect(jsonPath("$.cor").value("Branco"));
        mvc.perform(put("/api/veiculos/17").contentType("application/json").content(BODY.replace("Branco", "Azul")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.cor").value("Azul"));
        mvc.perform(delete("/api/veiculos/17")).andExpect(status().isNoContent());
        mvc.perform(get("/api/veiculos/17")).andExpect(status().isOk()).andExpect(jsonPath("$.ativo").value(false));
        verify(repository, times(3)).save(any(VeiculoEntity.class));
    }

    @Test
    void rejeitarCorpoInvalidoSemPersistir() throws Exception {
        mvc.perform(post("/api/veiculos").contentType("application/json").content("{}")).andExpect(status().isBadRequest());
        verify(repository, never()).save(any());
    }

    @Test
    void consultaAusenteNaoPodeRetornarSucesso() throws Exception {
        mvc.perform(get("/api/veiculos/999")).andExpect(status().is4xxClientError());
        verify(repository).findById(999L);
        verify(repository, never()).save(any());
    }
}
