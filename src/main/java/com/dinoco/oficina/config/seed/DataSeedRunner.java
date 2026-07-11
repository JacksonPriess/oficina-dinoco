package com.dinoco.oficina.config.seed;

import com.dinoco.oficina.catalogoproduto.application.usecases.commands.criar.CriarProdutoCommand;
import com.dinoco.oficina.catalogoproduto.application.usecases.commands.criar.CriarProdutoUseCase;
import com.dinoco.oficina.catalogoproduto.domain.TipoProduto;
import com.dinoco.oficina.catalogoservico.application.gateways.ServicoCommandGateway;
import com.dinoco.oficina.catalogoservico.domain.Servico;
import com.dinoco.oficina.cliente.application.gateways.ClienteCommandGateway;
import com.dinoco.oficina.cliente.application.gateways.ClienteQueryGateway;
import com.dinoco.oficina.cliente.domain.Cliente;
import com.dinoco.oficina.cliente.domain.Endereco;
import com.dinoco.oficina.funcionario.application.gateways.FuncionarioCommandGateway;
import com.dinoco.oficina.ordemservico.application.usecases.commands.abrir.AbrirOrdemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.abrir.AbrirOrdemServicoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemproduto.AdicionarItemProdutoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemproduto.AdicionarItemProdutoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemservico.AdicionarItemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemservico.AdicionarItemServicoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.aprovar.AprovarOrcamentoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.aprovar.AprovarOrcamentoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluir.ConcluirOrdemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluir.ConcluirOrdemServicoUseCase;
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

@Slf4j
@Component
@Profile("dev")
public class DataSeedRunner implements CommandLineRunner {


    private final ClienteCommandGateway clienteGateway;
    private final ClienteQueryGateway clienteQueryGateway;
    private final VeiculoCommandGateway veiculoGateway;
    private final ServicoCommandGateway servicoGateway;
    private final FuncionarioCommandGateway funcionarioGateway;

    private final CriarProdutoUseCase criarProdutoUseCase;
    private final AbrirOrdemServicoUseCase abrirOrdemServicoUseCase;
    private final IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase;
    private final AdicionarItemProdutoUseCase adicionarItemProdutoUseCase;
    private final AdicionarItemServicoUseCase adicionarItemServicoUseCase;
    private final ConcluirDiagnosticoUseCase concluirDiagnosticoUseCase;
    private final EnviarOrcamentoUseCase enviarOrcamentoUseCase;
    private final AprovarOrcamentoUseCase aprovarOrcamentoUseCase;
    private final IniciarExecucaoUseCase iniciarExecucaoUseCase;
    private final IniciarExecucaoItemServicoUseCase iniciarExecucaoItemServicoUseCase;
    private final ConcluirExecucaoItemServicoUseCase concluirExecucaoItemServicoUseCase;
    private final FinalizarExecucaoUseCase finalizarExecucaoUseCase;
    private final ConcluirOrdemServicoUseCase concluirOrdemServicoUseCase;

    public DataSeedRunner(
            ClienteCommandGateway clienteGateway,
            ClienteQueryGateway clienteQueryGateway,
            VeiculoCommandGateway veiculoGateway,
            ServicoCommandGateway servicoGateway,
            FuncionarioCommandGateway funcionarioGateway,
            CriarProdutoUseCase criarProdutoUseCase,
            AbrirOrdemServicoUseCase abrirOrdemServicoUseCase,
            IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase,
            AdicionarItemProdutoUseCase adicionarItemProdutoUseCase,
            AdicionarItemServicoUseCase adicionarItemServicoUseCase,
            ConcluirDiagnosticoUseCase concluirDiagnosticoUseCase,
            EnviarOrcamentoUseCase enviarOrcamentoUseCase,
            AprovarOrcamentoUseCase aprovarOrcamentoUseCase,
            IniciarExecucaoUseCase iniciarExecucaoUseCase,
            IniciarExecucaoItemServicoUseCase iniciarExecucaoItemServicoUseCase,
            ConcluirExecucaoItemServicoUseCase concluirExecucaoItemServicoUseCase,
            FinalizarExecucaoUseCase finalizarExecucaoUseCase,
            ConcluirOrdemServicoUseCase concluirOrdemServicoUseCase) {
        this.clienteGateway = clienteGateway;
        this.clienteQueryGateway = clienteQueryGateway;
        this.veiculoGateway = veiculoGateway;
        this.servicoGateway = servicoGateway;
        this.funcionarioGateway = funcionarioGateway;
        this.criarProdutoUseCase = criarProdutoUseCase;
        this.abrirOrdemServicoUseCase = abrirOrdemServicoUseCase;
        this.iniciarDiagnosticoUseCase = iniciarDiagnosticoUseCase;
        this.adicionarItemProdutoUseCase = adicionarItemProdutoUseCase;
        this.adicionarItemServicoUseCase = adicionarItemServicoUseCase;
        this.concluirDiagnosticoUseCase = concluirDiagnosticoUseCase;
        this.enviarOrcamentoUseCase = enviarOrcamentoUseCase;
        this.aprovarOrcamentoUseCase = aprovarOrcamentoUseCase;
        this.iniciarExecucaoUseCase = iniciarExecucaoUseCase;
        this.iniciarExecucaoItemServicoUseCase = iniciarExecucaoItemServicoUseCase;
        this.concluirExecucaoItemServicoUseCase = concluirExecucaoItemServicoUseCase;
        this.finalizarExecucaoUseCase = finalizarExecucaoUseCase;
        this.concluirOrdemServicoUseCase = concluirOrdemServicoUseCase;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (clienteQueryGateway.existePorDocumento("00018563082")) {
            log.info("Banco já populado. Pulando Seeding.");
            return;
        }
        log.info("[INICIO] Executando Fábrica de Dados da Oficina Dinoco...");

        // 1. Dicionários de IDs para reaproveitamento nos cenários
        List<Long> clientes = popularClientes();
        List<Long> veiculos = popularVeiculos();
        List<Long> produtos = popularProdutos();
        List<Long> servicos = popularServicos();

        // 2. Criação das Ordens de Serviço ricas em detalhes
        popularOrdensDeServico(clientes, veiculos, produtos, servicos);

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

    private List<Long> popularProdutos() {
        log.info("-> Cadastrando Produtos e Insumos no Estoque...");
        List<Long> ids = new ArrayList<>();
        ids.add(salvarProduto("Filtro de Óleo", TipoProduto.PECA, "Fram", "PH10060", "Ford Focus", 20.0, 25.0, 45.0)); // 0
        ids.add(salvarProduto("Óleo Motor 5W30 1L", TipoProduto.INSUMO, "Castrol", "5W30", "Motores gasolina", 50.0, 30.0, 55.0)); // 1
        ids.add(salvarProduto("Pastilha de Freio Dianteira", TipoProduto.PECA, "Bosch", "BP1234", "Ford Focus", 15.0, 80.0, 150.0)); // 2
        ids.add(salvarProduto("Filtro de Ar", TipoProduto.PECA, "Tecfil", "ARL1234", "Ford Focus", 20.0, 35.0, 65.0)); // 3
        ids.add(salvarProduto("Vela de Ignição", TipoProduto.PECA, "NGK", "TR6B-13", "Motores flex", 2.0, 25.0, 60.0)); // 4
        ids.add(salvarProduto("Correia Dentada", TipoProduto.PECA, "Continental", "CT1074", "Motor 1.6", 10.0, 120.0, 220.0)); // 5
        ids.add(salvarProduto("Fluido de Freio DOT 4", TipoProduto.INSUMO, "Varga", "DOT4", "Universal", 25.0, 20.0, 40.0)); // 6
        ids.add(salvarProduto("Aditivo Radiador", TipoProduto.INSUMO, "Paraflu", "RAD123", "Universal", 30.0, 18.0, 35.0)); // 7
        ids.add(salvarProduto("Bico Injetor Cleaner", TipoProduto.INSUMO, "Car80", "C80", "Universal", 20.0, 15.0, 35.0)); // 8
        return ids;
    }

    private List<Long> popularServicos() {
        log.info("-> Cadastrando Serviços e Mão de Obra...");
        List<Long> ids = new ArrayList<>();
        ids.add(salvarServico("Mão de obra - troca de filtro de óleo", 40.00, 20)); // 0
        ids.add(salvarServico("Mão de obra - troca de óleo", 50.00, 30)); // 1
        ids.add(salvarServico("Mão de obra - troca de pastilhas de freio", 120.00, 60)); // 2
        ids.add(salvarServico("Mão de obra - troca de filtro de ar", 35.00, 20)); // 3
        ids.add(salvarServico("Mão de obra - troca de velas", 80.00, 50)); // 4
        ids.add(salvarServico("Mão de obra - troca de correia dentada", 300.00, 180)); // 5
        ids.add(salvarServico("Mão de obra - sangria / troca de fluido de freio", 90.00, 45)); // 6
        ids.add(salvarServico("Mão de obra - troca de fluido do radiador", 100.00, 60)); // 7
        ids.add(salvarServico("Mão de obra - limpeza de bico injetor", 180.00, 90)); // 8
        return ids;
    }

    private void popularOrdensDeServico(List<Long> clientes, List<Long> veiculos, List<Long> produtos, List<Long> servicos) {
        log.info("-> Gerando Ordens de Serviço (Estados Variados)...");

        /**
         * Cenário 1 : Cenário principal completo - Todos os produtos contem saldo positivo no estoque.
         */
        // Passo 1: Abrir OS:
        Long ordemServicoA = abrirOsSemItens(
                clientes.get(0),
                veiculos.get(0),
                45500, "Preventiva trocar filtro e óleo");

        log.info("-> Ciclo OS - Aberta.");
        // Passo 2: Iniciar Diagnóstico OS:
        iniciarDiagnostico(ordemServicoA);
        log.info("-> Ciclo OS - Diagnóstico iniciado.");

        // Passo 3: Adicionar itens de serviço e itens de produto
        adicionarPecaEServico(ordemServicoA, List.of(produtos.get(0), produtos.get(1)), List.of(servicos.get(0), servicos.get(1)));

        // Passo 4: Concluir Diagnóstico OS:
        concluirDiagnostico(ordemServicoA, "Diagnóstico concluído com sucesso. Necessário trocar filtro e óleo.");
        log.info("-> Ciclo OS - Diagnóstico concluído.");

        // Passo 5: Enviar orçamento para o cliente
        enviarOrcamento(ordemServicoA);
        log.info("-> Ciclo OS - Orçamento enviado.");

        // Passo 6: Aprovar orçamento
        aprovarOrcamento(ordemServicoA);
        log.info("-> Ciclo OS - Orçamento aprovado.");

        // Passo 7: Iniciar execução da OS
        iniciarExecucaoOS(ordemServicoA);
        log.info("-> Ciclo OS - Iniciado execução.");

        // Passo 8: Iniciar execução e concluir itens de serviço.
        iniciarEConcluirExecucaoItemServico(ordemServicoA, 1L);
        iniciarEConcluirExecucaoItemServico(ordemServicoA, 2L);


        // Passo 9: Finalizar execução da OS
        finalizarExecucaoOS(ordemServicoA);
        log.info("-> Ciclo OS - Finalizado execução.");

        // Passo 10: Concluir execução da OS
        concluirExecucaoOS(ordemServicoA);
        log.info("-> Ciclo OS - Concluído.");

        /**
         * Cenário 2 : Cenário onde os produtos não tem saldo no estoque;
         */
        /*

        // OS 1: RECEBIDA (Carro puxando para o lado)
        abrirOsComItens(clientes.get(0), veiculos.get(0), 45000, "Carro puxando para o lado", null, null);

        // OS 2: EM DIAGNÓSTICO (Luz de freio acesa) -> TODO: Adicionar IniciarDiagnosticoUseCase depois
        abrirOsComItens(clientes.get(1), veiculos.get(1), 95000, "Luz de freio acesa", null, null);

        // OS 3: AGUARDANDO_ORCAMENTO (Preventiva filtro/óleo)
        abrirOsComItens(clientes.get(2), veiculos.get(2), 45500, "Preventiva trocar filtro e óleo",
                List.of(produtos.get(0), produtos.get(1)),
                List.of(servicos.get(0), servicos.get(1)));

        // OS 4: AGUARDANDO_APROVACAO (Luz de freio acesa / Pastilhas)
        abrirOsComItens(clientes.get(3), veiculos.get(3), 45500, "Luz de freio acesa",
                List.of(produtos.get(2)), List.of(servicos.get(2)));

        // OS 5: AGUARDANDO_EXECUCAO (Ar cheiro ruim / Filtro Ar)
        abrirOsComItens(clientes.get(4), veiculos.get(4), 45500, "Ar cheiro ruim",
                List.of(produtos.get(3)), List.of(servicos.get(3)));

        // OS 6: AGUARDANDO_FORNECEDOR (Falhando engasgando / Velas)
        abrirOsComItens(clientes.get(4), veiculos.get(4), 45500, "Carro está falhando engasgando",
                List.of(produtos.get(4)), List.of(servicos.get(4)));

        // OS 7: CONCLUÍDA (Barulho estranho correia)
        abrirOsComItens(clientes.get(4), veiculos.get(4), 45500, "Barulho estranho de correia",
                List.of(produtos.get(5)), List.of(servicos.get(5))); // Passando true para finalizar a OS

        // OS 8: CONCLUÍDA (Duplicada para gerar volume de métricas)
        abrirOsComItens(clientes.get(5), veiculos.get(5), 45500, "Barulho estranho de correia",
                List.of(produtos.get(5)), List.of(servicos.get(5)));

         */
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

    private Long salvarProduto(String nome, TipoProduto tipo, String marca, String cod, String app, Double qtd, Double custo, Double venda) {
        var cmd = new CriarProdutoCommand(nome, tipo, marca, cod, app, BigDecimal.valueOf(custo), BigDecimal.valueOf(venda), BigDecimal.valueOf(qtd));
        return criarProdutoUseCase.executar(cmd).id();
    }

    private Long salvarServico(String desc, Double preco, int tempo) {
        return servicoGateway.salvar(new Servico(desc, BigDecimal.valueOf(preco), tempo)).getId();
    }

    private Long abrirOsComItens(Long clienteId, Long veiculoId, int km, String defeito, List<Long> produtos, List<Long> servicos) {
        var osCmd = new AbrirOrdemServicoCommand(clienteId, veiculoId, km, defeito);
        Long osId = abrirOrdemServicoUseCase.executar(osCmd).osId();

        if (servicos != null) {
            servicos.forEach(servicoId -> adicionarItemServicoUseCase.executar(new AdicionarItemServicoCommand(osId, servicoId, null)));
        }
        if (produtos != null) {
            produtos.forEach(produtoId -> adicionarItemProdutoUseCase.executar(new AdicionarItemProdutoCommand(osId, produtoId, BigDecimal.ONE)));
        }

        return osId;
    }

    private Long abrirOsSemItens(Long clienteId, Long veiculoId, int km, String defeito) {
        var osCmd = new AbrirOrdemServicoCommand(clienteId, veiculoId, km, defeito);
        Long osId = abrirOrdemServicoUseCase.executar(osCmd).osId();
        return osId;
    }

    private void iniciarDiagnostico(Long osId) {
        var command = new IniciarDiagnosticoCommand(osId);
        iniciarDiagnosticoUseCase.executar(command);
    }

    private void adicionarPecaEServico(Long osId, List<Long> produtos, List<Long> servicos) {
        servicos.forEach(servicoId -> adicionarItemServicoUseCase.executar(new AdicionarItemServicoCommand(osId, servicoId, null)));
        log.info("-> Ciclo OS - Adicionado items de serviço na OS.");
        produtos.forEach(produtoId -> adicionarItemProdutoUseCase.executar(new AdicionarItemProdutoCommand(osId, produtoId, BigDecimal.ONE)));
        log.info("-> Ciclo OS - Adicionado items de produto na OS.");
    }

    private void concluirDiagnostico(Long osId, String laudo) {
        var command = new ConcluirDiagnosticoCommand(osId, laudo);
        concluirDiagnosticoUseCase.executar(command);
    }

    private void enviarOrcamento(Long osId) {
        enviarOrcamentoUseCase.executar(new EnviarOrcamentoCommand(osId));
    }

    private void aprovarOrcamento(Long osId) {
        aprovarOrcamentoUseCase.executar(new AprovarOrcamentoCommand(osId));
    }

    private void iniciarExecucaoOS(Long osId) {
        iniciarExecucaoUseCase.executar(new IniciarExecucaoCommand(osId));
    }

    private void iniciarEConcluirExecucaoItemServico(Long osId, long itemId) {
        iniciarExecucaoItemServicoUseCase.executar(new IniciarExecucaoItemServicoCommand(osId, itemId));
        log.info("-> Ciclo OS - Iniciado execução item ." + itemId);

        concluirExecucaoItemServicoUseCase.executar(new ConcluirExecucaoItemServicoCommand(osId, itemId, LocalDateTime.now().plusHours(1) ));
        log.info("-> Ciclo OS - Concluido execução item ." + itemId);
    }

    private void finalizarExecucaoOS(Long osId) {
        finalizarExecucaoUseCase.executar(new FinalizarExecucaoCommand(osId));
    }

    private void concluirExecucaoOS(Long osId) {
        concluirOrdemServicoUseCase.executar(new ConcluirOrdemServicoCommand(osId));
    }

}
