package com.dinoco.oficina.service;

//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;

import com.dinoco.oficina.dto.OrdemServicoRequestDto;
import com.dinoco.oficina.entity.Cliente;
import com.dinoco.oficina.entity.OrdemServico;
import com.dinoco.oficina.entity.Veiculo;
import com.dinoco.oficina.enums.StatusOS;
import com.dinoco.oficina.repository.ClienteRepository;
import com.dinoco.oficina.repository.OrdemServicoRepository;
import com.dinoco.oficina.repository.VeiculoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers //Se comunica com o daemon do Docker na sua máquina. O Docker baixa (se necessário) e sobe um contêiner real do PostgreSQL na porta dinâmica disponível.
@Transactional // Garante que os dados sejam limpos (rollback) após a execução do teste
public class OrdemServicoServiceIT {

    // Sobe o contêiner do PostgreSQL na versão desejada
    @Container
    @ServiceConnection // O Spring intercepta os dados do contêiner que acabou de subir (URL, porta dinâmica, usuário e senha) e os injeta diretamente no DataSource da sua aplicação, sem você precisar configurar um application-test.yml.
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    // Repositórios injetados exclusivamente para preparar a massa de dados e fazer asserções

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private OrdemServicoService ordemServicoService;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Test
    void deveSalvarOrdemServicoComSucessoNoBanco() {
        // 1. Arrange: Preparação direta no banco ignorando as regras de negócio de outras Services
        Cliente cliente = new Cliente();
        cliente.setNome("Maria Oliveira");
        cliente.setEmail("maria@gmail.com");
        cliente.setTelefone("30256498");
        cliente.setDocumento("00000000191");
        cliente.setTipoPessoa("F");
        cliente.setAtivo(true);
        cliente.setDataCriacao(LocalDateTime.now());
        cliente = clienteRepository.save(cliente);

        Veiculo veiculo = new Veiculo();
        veiculo.setModelo("Corolla");
        veiculo.setMarca("Toyota");
        veiculo.setPlaca("XYZ-9876");
        veiculo.setAtivo(true);
        veiculo.setDataCriacao(LocalDateTime.now());
        veiculo = veiculoRepository.save(veiculo);

        OrdemServicoRequestDto novaOs = new OrdemServicoRequestDto(cliente.getId(),veiculo.getId(), 133000, "Carro está demorando para ligar");

        // 2. Act: Execução exclusiva da Service alvo
        var osSalva = ordemServicoService.abrirOs(novaOs);

        // 3. Assert: Validações de integridade contra o banco físico
        assertNotNull(osSalva.id(), "A persistência deve gerar um ID válido para a OS");

        OrdemServico osNoBanco = ordemServicoRepository.findById(osSalva.id()).orElse(null);

        assertNotNull(osNoBanco, "A OS deve ser encontrada na base de dados do PostgreSQL");
        assertEquals(StatusOS.RECEBIDA, osNoBanco.getStatus());

        assertNotNull(osNoBanco.getCliente());
        assertEquals(cliente.getId(), osNoBanco.getCliente().getId());

        assertNotNull(osNoBanco.getVeiculo());
        assertEquals(veiculo.getId(), osNoBanco.getVeiculo().getId());
    }
}
