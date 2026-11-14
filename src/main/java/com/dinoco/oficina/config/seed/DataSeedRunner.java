package com.dinoco.oficina.config.seed;

import com.dinoco.oficina.cliente.application.gateways.ClienteCommandGateway;
import com.dinoco.oficina.cliente.application.gateways.ClienteQueryGateway;
import com.dinoco.oficina.cliente.domain.Cliente;
import com.dinoco.oficina.cliente.domain.Endereco;
import com.dinoco.oficina.estoque.application.usecases.commands.registrarentrada.RegistrarEntradaCommand;
import com.dinoco.oficina.estoque.application.usecases.commands.registrarentrada.RegistrarEntradaUseCase;
import com.dinoco.oficina.funcionario.application.gateways.FuncionarioCommandGateway;
import com.dinoco.oficina.ordemservico.application.usecases.commands.abrir.AbrirOrdemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.abrir.AbrirOrdemServicoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemproduto.AdicionarItemProdutoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemproduto.AdicionarItemProdutoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemservico.AdicionarItemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemservico.AdicionarItemServicoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemproduto.AlterarItemProdutoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemproduto.AlterarItemProdutoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.aprovar.AprovarOrcamentoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.aprovar.AprovarOrcamentoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirdiagnostico.ConcluirDiagnosticoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirdiagnostico.ConcluirDiagnosticoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirexecucaoitemservico.ConcluirExecucaoItemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirexecucaoitemservico.ConcluirExecucaoItemServicoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.enviarorcamento.EnviarOrcamentoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.enviarorcamento.EnviarOrcamentoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.finalizarexecucao.FinalizarExecucaoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.finalizarexecucao.FinalizarExecucaoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciardiagnostico.IniciarDiagnosticoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciardiagnostico.IniciarDiagnosticoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucao.IniciarExecucaoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucao.IniciarExecucaoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucaoitemservico.IniciarExecucaoItemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucaoitemservico.IniciarExecucaoItemServicoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.verificarestoque.VerificarEstoqueCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.verificarestoque.VerificarEstoqueUseCase;
import com.dinoco.oficina.veiculo.application.gateways.VeiculoCommandGateway;
import com.dinoco.oficina.veiculo.domain.Veiculo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Profile("dev")
public class DataSeedRunner implements CommandLineRunner {


    private final ClienteCommandGateway clienteGateway;
    private final ClienteQueryGateway clienteQueryGateway;
    private final VeiculoCommandGateway veiculoGateway;
    private final FuncionarioCommandGateway funcionarioGateway;

    private final AbrirOrdemServicoUseCase abrirOrdemServicoUseCase;
    private final IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase;
    private final AdicionarItemProdutoUseCase adicionarItemProdutoUseCase;
    private final AlterarItemProdutoUseCase alterarItemProdutoUseCase;
    private final AdicionarItemServicoUseCase adicionarItemServicoUseCase;
    private final ConcluirDiagnosticoUseCase concluirDiagnosticoUseCase;
    private final EnviarOrcamentoUseCase enviarOrcamentoUseCase;
    private final AprovarOrcamentoUseCase aprovarOrcamentoUseCase;
    private final IniciarExecucaoUseCase iniciarExecucaoUseCase;
    private final IniciarExecucaoItemServicoUseCase iniciarExecucaoItemServicoUseCase;
    private final ConcluirExecucaoItemServicoUseCase concluirExecucaoItemServicoUseCase;
    private final FinalizarExecucaoUseCase finalizarExecucaoUseCase;

    private final RegistrarEntradaUseCase registrarEntradaUseCase;
    private final VerificarEstoqueUseCase verificarEstoqueUseCase;

    public DataSeedRunner(
            ClienteCommandGateway clienteGateway,
            ClienteQueryGateway clienteQueryGateway,
            VeiculoCommandGateway veiculoGateway,
            FuncionarioCommandGateway funcionarioGateway,
            AbrirOrdemServicoUseCase abrirOrdemServicoUseCase,
            IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase,
            AdicionarItemProdutoUseCase adicionarItemProdutoUseCase,
            AlterarItemProdutoUseCase alterarItemProdutoUseCase,
            AdicionarItemServicoUseCase adicionarItemServicoUseCase,
            ConcluirDiagnosticoUseCase concluirDiagnosticoUseCase,
            EnviarOrcamentoUseCase enviarOrcamentoUseCase,
            AprovarOrcamentoUseCase aprovarOrcamentoUseCase,
            IniciarExecucaoUseCase iniciarExecucaoUseCase,
            IniciarExecucaoItemServicoUseCase iniciarExecucaoItemServicoUseCase,
            ConcluirExecucaoItemServicoUseCase concluirExecucaoItemServicoUseCase,
            FinalizarExecucaoUseCase finalizarExecucaoUseCase,
            RegistrarEntradaUseCase registrarEntradaUseCase,
            VerificarEstoqueUseCase verificarEstoqueUseCase) {
        this.clienteGateway = clienteGateway;
        this.clienteQueryGateway = clienteQueryGateway;
        this.veiculoGateway = veiculoGateway;
        this.funcionarioGateway = funcionarioGateway;
        this.abrirOrdemServicoUseCase = abrirOrdemServicoUseCase;
        this.iniciarDiagnosticoUseCase = iniciarDiagnosticoUseCase;
        this.adicionarItemProdutoUseCase = adicionarItemProdutoUseCase;
        this.alterarItemProdutoUseCase = alterarItemProdutoUseCase;
        this.adicionarItemServicoUseCase = adicionarItemServicoUseCase;
        this.concluirDiagnosticoUseCase = concluirDiagnosticoUseCase;
        this.enviarOrcamentoUseCase = enviarOrcamentoUseCase;
        this.aprovarOrcamentoUseCase = aprovarOrcamentoUseCase;
        this.iniciarExecucaoUseCase = iniciarExecucaoUseCase;
        this.iniciarExecucaoItemServicoUseCase = iniciarExecucaoItemServicoUseCase;
        this.concluirExecucaoItemServicoUseCase = concluirExecucaoItemServicoUseCase;
        this.finalizarExecucaoUseCase = finalizarExecucaoUseCase;
        this.registrarEntradaUseCase = registrarEntradaUseCase;
        this.verificarEstoqueUseCase = verificarEstoqueUseCase;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (clienteQueryGateway.existePorDocumento("00018563082")) {
            log.info("Banco já populado. Pulando Seeding.");
            return;
        }
        log.info("[INICIO] Executando Fábrica de Dados da Oficina Dinoco...");

        popularClientes();
        popularVeiculos();

        log.info("[SUCESSO] Ambiente DEV pronto para avaliação!");
    }

    private List<Long> popularClientes() {
        log.info("-> Criando Clientes (Pessoas Físicas e Jurídicas)...");
        List<Long> ids = new ArrayList<>();
        ids.add(salvarCliente("F", "00018563082", null, "João Silva", null, "joao.silva@email.com"));
        ids.add(salvarCliente("F", "27695093068", null, "Maria Oliveira", null, "maria.oliveira@email.com"));
        ids.add(salvarCliente("F", "04905293057", null, "Carlos Souza", null, "carlos.souza@email.com"));
        ids.add(salvarCliente("F", "07381222035", null, "Ana Pereira", null, "ana.pereira@email.com"));
        ids.add(salvarCliente("F", "66906704060", null, "Fernanda Costa", null, "fernanda.costa@email.com"));
        ids.add(salvarCliente("J", "69798514000130", "123456789", "Auto Mecânica Silva LTDA", "Mecânica Silva", "contato@mecanicasilva.com"));
        ids.add(salvarCliente("J", "16423841000141", "987654321", "Oficina Rápida ME", "Oficina Rápida", "contato@oficinarapida.com"));
        ids.add(salvarCliente("J", "70770430000178", "456123789", "Centro Automotivo Brasil LTDA", "Auto Brasil", "contato@autobrasil.com"));
        ids.add(salvarCliente("J", "44103564000125", "321654987", "Car Service Express LTDA", "Car Express", "contato@carexpress.com"));
        ids.add(salvarCliente("J", "12ABC34501DE35", "789123456", "Top Motors Comércio LTDA", "Top Motors", "contato@topmotors.com"));
        return ids;
    }

    private List<Long> popularVeiculos() {
        log.info("-> Criando Veículos (Frota diversificada)...");
        List<Long> ids = new ArrayList<>();
        ids.add(salvarVeiculo("FOC2012", "Ford", "Focus Hatch Titanium", 2012, 2013, "Prata", "9BF123", "2.0 Duratec"));
        ids.add(salvarVeiculo("FOC1600", "Ford", "Focus Sedan GLX", 2011, 2011, "Preto", "9BF456", "1.6 Sigma"));
        ids.add(salvarVeiculo("ABC1D23", "Volkswagen", "Gol", 2020, 2021, "Branco", "9BW789", "1.0 MPI"));
        ids.add(salvarVeiculo("XYZ9A87", "Chevrolet", "Onix", 2019, 2019, "Vermelho", "9BG012", "1.0 Turbo"));
        ids.add(salvarVeiculo("BRA2E19", "Toyota", "Corolla", 2022, 2023, "Cinza", "9BR345", "2.0 Dynamic Force"));
        ids.add(salvarVeiculo("KLA5H12", "Hyundai", "HB20", 2018, 2018, "Azul", "9BH678", "1.6 Kappa"));
        ids.add(salvarVeiculo("JEP0I21", "Jeep", "Compass", 2021, 2021, "Diesel", "9BJ901", "2.0 Multijet"));
        ids.add(salvarVeiculo("HON8B45", "Honda", "Civic LXR", 2014, 2015, "Branco", "9BH234", "2.0 i-VTEC"));
        ids.add(salvarVeiculo("REN4C67", "Renault", "Sandero Stepway", 2017, 2017, "Laranja", "9BR567", "1.6 SCe"));
        ids.add(salvarVeiculo("FIA2F34", "Fiat", "Argo", 2022, 2022, "Preto", "9BD890", "1.3 Firefly"));
        ids.add(salvarVeiculo("NIS9G10", "Nissan", "Kicks", 2020, 2021, "Cinza Grafite", "9BN123", "1.6 HR16DE"));
        ids.add(salvarVeiculo("FOR0F01", "Ford", "Ka", 2015, 2015, "Prata", "9BF432", "1.0 TiVCT"));
        ids.add(salvarVeiculo("PEU5T66", "Peugeot", "208", 2023, 2024, "Azul Quasar", "9BP555", "1.0 Firefly"));
        ids.add(salvarVeiculo("CIT9R11", "Citroën", "C3", 2012, 2013, "Preto", "9BC777", "1.5 8v"));
        ids.add(salvarVeiculo("BMW3I30", "BMW", "320i", 2021, 2022, "Branco M", "9BM111", "2.0 B48"));
        return ids;
    }



    private Long salvarCliente(String tipo, String doc, String ie, String nome, String fantasia, String email) {
        var cliente = new Cliente(tipo, doc, nome, ie, fantasia, email, "47999990000");
        cliente.adicionarEndereco(new Endereco("88385000", "Rua Central", "100", "", "Centro", "Penha", "SC"));
        return clienteGateway.salvar(cliente).getId();
    }

    private Long salvarVeiculo(String placa, String marca, String modelo, Integer fab, Integer mod, String cor, String chassi, String motor) {
        var veiculo = new Veiculo(placa, marca, modelo, fab, mod, cor, chassi, motor);
        return veiculoGateway.salvar(veiculo).getId();
    }



}