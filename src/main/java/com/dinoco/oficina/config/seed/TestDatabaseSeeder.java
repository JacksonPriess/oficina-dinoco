package com.dinoco.oficina.config.seed;

import com.dinoco.oficina.dto.*;
import com.dinoco.oficina.enums.TipoProduto;
import com.dinoco.oficina.repository.ClienteRepository;
import com.dinoco.oficina.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;

@Component
@Profile("dev")
public class TestDatabaseSeeder implements CommandLineRunner {

    private final ClienteRepository clienteRepository;

    private final ClienteService clienteService;
    private final VeiculoService veiculoService;
    private final ServicoService servicoService;
    private final ProdutoService produtoService;
    private final OrdemServicoService ordemServicoService;
    private final ItemOSServicoService itemOSServicoService;
    private final ItemOSProdutoService itemOSServicoProduto;

    public TestDatabaseSeeder(ClienteRepository clienteRepository, ClienteService clienteService,
                              VeiculoService veiculoService, ServicoService servicoService,
                              ProdutoService produtoService, OrdemServicoService ordemServicoService,
                              ItemOSServicoService itemOSServicoService, ItemOSProdutoService itemOSServicoProduto) {
        this.clienteRepository = clienteRepository;
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
        this.servicoService = servicoService;
        this.produtoService = produtoService;
        this.ordemServicoService = ordemServicoService;
        this.itemOSServicoService = itemOSServicoService;
        this.itemOSServicoProduto = itemOSServicoProduto;
    }

    @Override
    public void run(String... args) throws Exception {
        if (clienteRepository.count() > 0) {
            System.out.println("✅ Banco já populado. Pulando Seeding.");
            return;
        }
        System.out.println("[INICIO] Executando Fábrica de Dados da Oficina Dinoco...");

        popularClientes();
        popularVeiculos();
        popularProdutosEServicos();
        popularOrdensDeServico();

        System.out.println("✅ [SUCESSO] Ambiente DEV pronto para avaliação!");
    }

    private void popularProdutosEServicos() {
        System.out.println("🔧 Criando Produtos e Serviços relacionados...");

        // 1
        cadastrarProduto("Filtro de Óleo", TipoProduto.PECA, "Fram", "PH10060", "Ford Focus", 20.0, 25.0, 45.0);
        cadastrarServico("Mão de obra - troca de filtro de óleo", 40.00, 20);
        // 2
        cadastrarProduto("Óleo Motor 5W30 1L", TipoProduto.INSUMO, "Castrol", "5W30", "Motores gasolina", 50.0, 30.0, 55.0);
        cadastrarServico("Mão de obra - troca de óleo", 50.00, 30);
        // 3
        cadastrarProduto("Pastilha de Freio Dianteira", TipoProduto.PECA, "Bosch", "BP1234", "Ford Focus", 15.0, 80.0, 150.0);
        cadastrarServico("Mão de obra - troca de pastilhas de freio", 120.00, 60);
        // 4
        cadastrarProduto("Filtro de Ar", TipoProduto.PECA, "Tecfil", "ARL1234", "Ford Focus", 20.0, 35.0, 65.0);
        cadastrarServico("Mão de obra - troca de filtro de ar", 35.00, 20);
        // 5
        cadastrarProduto("Vela de Ignição", TipoProduto.PECA, "NGK", "TR6B-13", "Motores flex", 2.0, 25.0, 60.0);
        cadastrarServico("Mão de obra - troca de velas", 80.00, 50);
        // 6
        cadastrarProduto("Correia Dentada", TipoProduto.PECA, "Continental", "CT1074", "Motor 1.6", 10.0, 120.0, 220.0);
        cadastrarServico("Mão de obra - troca de correia dentada", 300.00, 180);
        // 7
        cadastrarProduto("Fluido de Freio DOT 4", TipoProduto.INSUMO, "Varga", "DOT4", "Universal", 25.0, 20.0, 40.0);
        cadastrarServico("Mão de obra - sangria / troca de fluido de freio", 90.00, 45);
        // 8
        cadastrarProduto("Aditivo Radiador", TipoProduto.INSUMO, "Paraflu", "RAD123", "Universal", 30.0, 18.0, 35.0);
        cadastrarServico("Mão de obra - troca de fluido do radiador", 100.00, 60);
        // 9
        cadastrarProduto("Bico Injetor Cleaner", TipoProduto.INSUMO, "Car80", "C80", "Universal", 20.0, 15.0, 35.0);
        cadastrarServico("Mão de obra - limpeza de bico injetor", 180.00, 90);
        System.out.println("✅ Produtos e serviços criados com relacionamento lógico.");
    }

    private void cadastrarServico(String descricao, double preco, int tempo) {
        ServicoRequestDto dto = new ServicoRequestDto(
                descricao,
                BigDecimal.valueOf(preco),
                tempo
        );

        servicoService.criar(dto);
    }

    private void cadastrarProduto(String nome, TipoProduto tipo, String marca, String codigo,
                                  String aplicacao, Double qtd, Double custo, Double venda) {

        ProdutoRequestDto dto = new ProdutoRequestDto(
                nome,
                tipo,
                marca,
                codigo,
                aplicacao,
                BigDecimal.valueOf(qtd),
                BigDecimal.valueOf(custo),
                BigDecimal.valueOf(venda)
        );

        produtoService.criar(dto);
    }

    private void popularClientes() {
        System.out.println("Criando clientes...");
        cadastrarCliente("F", "00018563082", null, "João Silva", null, "joao.silva@email.com", "47999990001");
        cadastrarCliente("F", "27695093068", null, "Maria Oliveira", null, "maria.oliveira@email.com", "47999990002");
        cadastrarCliente("F", "04905293057", null, "Carlos Souza", null, "carlos.souza@email.com", "47999990003");
        cadastrarCliente("F", "07381222035", null, "Ana Pereira", null, "ana.pereira@email.com", "47999990004");
        cadastrarCliente("F", "66906704060", null, "Fernanda Costa", null, "fernanda.costa@email.com", "47999990005");
        cadastrarCliente("J", "69798514000130", "123456789", "Auto Mecânica Silva LTDA", "Mecânica Silva", "contato@mecanicasilva.com", "47999990006");
        cadastrarCliente("J", "16423841000141", "987654321", "Oficina Rápida ME", "Oficina Rápida", "contato@oficinarapida.com", "47999990007");
        cadastrarCliente("J", "70770430000178", "456123789", "Centro Automotivo Brasil LTDA", "Auto Brasil", "contato@autobrasil.com", "47999990008");
        cadastrarCliente("J", "44103564000125", "321654987", "Car Service Express LTDA", "Car Express", "contato@carexpress.com", "47999990009");
        cadastrarCliente("J", "12ABC34501DE35", "789123456", "Top Motors Comércio LTDA", "Top Motors", "contato@topmotors.com", "47999990010");
        System.out.println("✅ Clientes criados com sucesso.");
    }

    private void cadastrarCliente(String tipoPessoa, String documento, String inscricaoEstadual, String nome, String nomeFantasia, String email, String telefone ) {
        EnderecoDto enderecoPadrao = new EnderecoDto("88385000", "Av. Nereu Ramos", "1000", "Sala 01", "Centro", "Penha", "SC");
        ClienteRequestDto dto = new ClienteRequestDto(
                tipoPessoa,
                documento,
                inscricaoEstadual,
                nome,
                nomeFantasia,
                email,
                telefone,
                List.of(enderecoPadrao)
        );
        clienteService.criar(dto);
    }

    private void popularVeiculos() {
        System.out.println("Criando Veículos...");

        Object[][] dadosVeiculos = {
                {"FOC2012", "Ford", "Focus Hatch Titanium", 2012, 2013, "Prata", "9BF123", "2.0 Duratec"},
                {"FOC1600", "Ford", "Focus Sedan GLX", 2011, 2011, "Preto", "9BF456", "1.6 Sigma"},
                {"ABC1D23", "Volkswagen", "Gol", 2020, 2021, "Branco", "9BW789", "1.0 MPI"},
                {"XYZ9A87", "Chevrolet", "Onix", 2019, 2019, "Vermelho", "9BG012", "1.0 Turbo"},
                {"BRA2E19", "Toyota", "Corolla", 2022, 2023, "Cinza", "9BR345", "2.0 Dynamic Force"},
                {"KLA5H12", "Hyundai", "HB20", 2018, 2018, "Azul", "9BH678", "1.6 Kappa"},
                {"JEP0I21", "Jeep", "Compass", 2021, 2021, "Diesel", "9BJ901", "2.0 Multijet"},
                {"HON8B45", "Honda", "Civic LXR", 2014, 2015, "Branco", "9BH234", "2.0 i-VTEC"},
                {"REN4C67", "Renault", "Sandero Stepway", 2017, 2017, "Laranja", "9BR567", "1.6 SCe"},
                {"FIA2F34", "Fiat", "Argo", 2022, 2022, "Preto", "9BD890", "1.3 Firefly"},
                {"NIS9G10", "Nissan", "Kicks", 2020, 2021, "Cinza Grafite", "9BN123", "1.6 HR16DE"},
                {"FOR0F01", "Ford", "Ka", 2015, 2015, "Prata", "9BF432", "1.0 TiVCT"},
                {"PEU5T66", "Peugeot", "208", 2023, 2024, "Azul Quasar", "9BP555", "1.0 Firefly"},
                {"CIT9R11", "Citroën", "C3", 2012, 2013, "Preto", "9BC777", "1.5 8v"},
                {"BMW3I30", "BMW", "320i", 2021, 2022, "Branco M", "9BM111", "2.0 B48"}
        };

        for (Object[] v : dadosVeiculos) {
            cadastrarVeiculo(
                    (String) v[0], (String) v[1], (String) v[2],
                    (Integer) v[3], (Integer) v[4], (String) v[5],
                    (String) v[6], (String) v[7]
            );
        }
        System.out.println("✅ 15 Veículos cadastrados");

    }

    private void cadastrarVeiculo(String placa, String marca, String modelo, Integer fabricacao,
                                  Integer anoMod, String cor, String chassi, String motor) {
        VeiculoRequestDto dto = new VeiculoRequestDto(
                placa, marca, modelo, fabricacao, anoMod, cor, chassi, motor );

        veiculoService.criar(dto);
    }

    private void popularOrdensDeServico() {
        System.out.println("Gerando Ordens de Serviço e executando lógicas...");
        // OS recém-aberta (Status: RECEBIDA)
        ordemServicoService.abrirOs(new OrdemServicoRequestDto(1L, 1L, 45000, "Carro puxando para o lado"));
        // OS em diagnóstico (Status: EM_DIAGNOSTICO)
        var osDiagnostico = ordemServicoService.abrirOs(new OrdemServicoRequestDto(2L, 2L, 95000, "Luz de freio acesa"));
        ordemServicoService.iniciarDiagnostico(osDiagnostico.id());
        // OS com diagnóstico concluido diagnóstico, (Status: AGUARDANDO_ORCAMENTO)
        var osDiagnoticoConcluido = ordemServicoService.abrirOs(new OrdemServicoRequestDto(3L, 3L, 45500, "Preventiva trocar filtro e óleo"));
        ordemServicoService.iniciarDiagnostico(osDiagnoticoConcluido.id());
        itemOSServicoService.adicionarItemServico(osDiagnoticoConcluido.id(), new ItemOSServicoAdicionarDto(1L, null));
        itemOSServicoProduto.adicionarItemProduto(osDiagnoticoConcluido.id(), new ItemOSProdutoAdicionarDto(1L, new BigDecimal(1)));
        itemOSServicoService.adicionarItemServico(osDiagnoticoConcluido.id(), new ItemOSServicoAdicionarDto(2L, null));
        itemOSServicoProduto.adicionarItemProduto(osDiagnoticoConcluido.id(), new ItemOSProdutoAdicionarDto(2L, new BigDecimal(4)));
        ordemServicoService.concluirDiagnostico(osDiagnoticoConcluido.id(), "Cárter rachado, necessário trocar óleo.");

        // OS orcamento revisado e enviado ao cliente (Status: AGUARDANDO_APROVACAO)
        var aguardandoAprovacao = ordemServicoService.abrirOs(new OrdemServicoRequestDto(4L, 4L, 45500, "Luz de freio acesa"));
        ordemServicoService.iniciarDiagnostico(aguardandoAprovacao.id());
        itemOSServicoProduto.adicionarItemProduto(aguardandoAprovacao.id(), new ItemOSProdutoAdicionarDto(3L, new BigDecimal(1)));
        itemOSServicoService.adicionarItemServico(aguardandoAprovacao.id(), new ItemOSServicoAdicionarDto(3L, null));
        ordemServicoService.concluirDiagnostico(aguardandoAprovacao.id(), "Trocar o disco de freio");
        ordemServicoService.enviarOrcamento(aguardandoAprovacao.id());

        // Os aprovada, contem estoque e AGUARDANDO_EXECUCAO
        var aprovadaComEstoque = ordemServicoService.abrirOs(new OrdemServicoRequestDto(5L, 5L, 45500, "Ar cheiro ruim"));
        ordemServicoService.iniciarDiagnostico(aprovadaComEstoque.id());
        itemOSServicoProduto.adicionarItemProduto(aprovadaComEstoque.id(), new ItemOSProdutoAdicionarDto(4L, new BigDecimal(4)));
        itemOSServicoService.adicionarItemServico(aprovadaComEstoque.id(), new ItemOSServicoAdicionarDto(4L, null));
        ordemServicoService.concluirDiagnostico(aprovadaComEstoque.id(), "Trocar filtro do Ar");
        ordemServicoService.enviarOrcamento(aprovadaComEstoque.id());
        ordemServicoService.aprovarOrcamento(aprovadaComEstoque.id()); // Com saldo em estoque.

        // Os aprovada, sem estoque e AGUARDANDO_FORNECEDOR
        var aprovadaSemEstoque = ordemServicoService.abrirOs(new OrdemServicoRequestDto(5L, 5L, 45500, "Carro está falhando engasgando"));
        ordemServicoService.iniciarDiagnostico(aprovadaSemEstoque.id());
        itemOSServicoProduto.adicionarItemProduto(aprovadaSemEstoque.id(), new ItemOSProdutoAdicionarDto(5L, new BigDecimal(4))); // Tem apenas 2 velas no estoque.
        itemOSServicoService.adicionarItemServico(aprovadaSemEstoque.id(), new ItemOSServicoAdicionarDto(5L, null));
        ordemServicoService.concluirDiagnostico(aprovadaSemEstoque.id(), "Trocar as velas");
        ordemServicoService.enviarOrcamento(aprovadaSemEstoque.id());
        ordemServicoService.aprovarOrcamento(aprovadaSemEstoque.id()); // Sem saldo suficiente em estoque

        // Os iniciada, servicos iniciados e concluidos, concluido OS
        var osConcluida = ordemServicoService.abrirOs(new OrdemServicoRequestDto(5L, 5L, 45500, "Barulho estranho de correia"));
        ordemServicoService.iniciarDiagnostico(osConcluida.id());
        itemOSServicoProduto.adicionarItemProduto(osConcluida.id(), new ItemOSProdutoAdicionarDto(6L, new BigDecimal(1)));
        itemOSServicoService.adicionarItemServico(osConcluida.id(), new ItemOSServicoAdicionarDto(6L, null));
        ordemServicoService.concluirDiagnostico(osConcluida.id(), "Correia está gasta");
        ordemServicoService.enviarOrcamento(osConcluida.id());
        ordemServicoService.aprovarOrcamento(osConcluida.id()); // Com saldo
        ordemServicoService.iniciarExecucaoOS(osConcluida.id());
        var ordemServicoDetalhadaResponseDto = ordemServicoService.buscarDetalhesPorCodigoRastreio(osConcluida.codigoRastreio());
        Long itemServicoId = ordemServicoDetalhadaResponseDto.servicos().getFirst().id();
        itemOSServicoService.iniciarExecucaoItemServico(itemServicoId);
        itemOSServicoService.concluirExecucaoItemServico(itemServicoId);
        ordemServicoService.finalizarExecucaoOS(osConcluida.id());
        ordemServicoService.entregarVeiculo(osConcluida.id());


    }
}