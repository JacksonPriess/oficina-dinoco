package com.dinoco.oficina.config.seed;

import com.dinoco.oficina.catalogoproduto.application.gateways.ProdutoCommandGateway;
import com.dinoco.oficina.catalogoproduto.application.usecases.commands.criar.CriarProdutoCommand;
import com.dinoco.oficina.catalogoproduto.application.usecases.commands.criar.CriarProdutoUseCase;
import com.dinoco.oficina.catalogoproduto.domain.Produto;
import com.dinoco.oficina.catalogoproduto.domain.TipoProduto;
import com.dinoco.oficina.catalogoservico.application.gateways.ServicoCommandGateway;
import com.dinoco.oficina.catalogoservico.domain.Servico;
import com.dinoco.oficina.cliente.application.gateways.ClienteCommandGateway;
import com.dinoco.oficina.cliente.application.gateways.ClienteQueryGateway;
import com.dinoco.oficina.cliente.domain.Cliente;
import com.dinoco.oficina.cliente.domain.Endereco;
import com.dinoco.oficina.funcionario.application.gateways.FuncionarioCommandGateway;
import com.dinoco.oficina.funcionario.domain.CargoFuncionario;
import com.dinoco.oficina.funcionario.domain.Funcionario;
import com.dinoco.oficina.ordemservico.application.usecases.commands.abrir.AbrirOrdemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.abrir.AbrirOrdemServicoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemproduto.AdicionarItemProdutoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemproduto.AdicionarItemProdutoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemservico.AdicionarItemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemservico.AdicionarItemServicoUseCase;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluir.ConcluirOrdemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluir.ConcluirOrdemServicoUseCase;
import com.dinoco.oficina.veiculo.application.gateways.VeiculoCommandGateway;
import com.dinoco.oficina.veiculo.domain.Veiculo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Component
@Profile("dev") // Executa apenas em perfil de desenvolvimento
public class DataSeedRunner implements CommandLineRunner {

    // 1. Gateways para Cadastros Base (Operações simples de escrita)
    private final ClienteCommandGateway clienteGateway;
    private final ClienteQueryGateway clienteQueryGateway;
    private final VeiculoCommandGateway veiculoGateway;
    private final ServicoCommandGateway servicoGateway;
    private final FuncionarioCommandGateway funcionarioGateway;

    // 2. Use Cases para Operações Complexas (Garantem regras de negócio e integridade)
    private final CriarProdutoUseCase criarProdutoUseCase;
    private final AbrirOrdemServicoUseCase abrirOrdemServicoUseCase;
    private final AdicionarItemProdutoUseCase adicionarItemProdutoUseCase;
    private final AdicionarItemServicoUseCase adicionarItemServicoUseCase;
    private final ConcluirOrdemServicoUseCase concluirOrdemServicoUseCase;

    public DataSeedRunner(
            ClienteCommandGateway clienteGateway,
            ClienteQueryGateway clienteQueryGateway,
            VeiculoCommandGateway veiculoGateway,
            ServicoCommandGateway servicoGateway,
            FuncionarioCommandGateway funcionarioGateway,
            CriarProdutoUseCase criarProdutoUseCase,
            AbrirOrdemServicoUseCase abrirOrdemServicoUseCase,
            AdicionarItemProdutoUseCase adicionarItemProdutoUseCase,
            AdicionarItemServicoUseCase adicionarItemServicoUseCase,
            ConcluirOrdemServicoUseCase concluirOrdemServicoUseCase) {
        this.clienteGateway = clienteGateway;
        this.clienteQueryGateway = clienteQueryGateway;
        this.veiculoGateway = veiculoGateway;
        this.servicoGateway = servicoGateway;
        this.funcionarioGateway = funcionarioGateway;
        this.criarProdutoUseCase = criarProdutoUseCase;
        this.abrirOrdemServicoUseCase = abrirOrdemServicoUseCase;
        this.adicionarItemProdutoUseCase = adicionarItemProdutoUseCase;
        this.adicionarItemServicoUseCase = adicionarItemServicoUseCase;
        this.concluirOrdemServicoUseCase = concluirOrdemServicoUseCase;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if ( clienteQueryGateway.existePorDocumento("27695093068") ){
            log.info("Banco já populado. Pulando Seeding.");
            return;
        }
        log.info("[INICIO] Iniciando o povoamento da base de dados (Seed) da Oficina Dinoco...");

        Long clienteId = popularClientes();
        Long veiculoId = popularVeiculos();
        Long produtoIdFiltroDeOleo = popularProdutos();
        Long servicoIdMaoObraTrocaFiltroOleo = popularServicos();

        popularOrdensServico(clienteId, veiculoId, produtoIdFiltroDeOleo, servicoIdMaoObraTrocaFiltroOleo);

        log.info("[SUCESSO] Ambiente DEV pronto para avaliação!");
    }

    private Long popularClientes() {
        log.info("Criando clientes...");
        clienteGateway.salvar(new Cliente("F", "27695093068", "Maria Oliveira", null, null, "maria.oliveira@email.com", "47999990002"));
        clienteGateway.salvar(new Cliente("J", "44103564000125", "Car Service Express LTDA", "Car Express", "321654987", "contato@carexpress.com", "47999990009"));
        clienteGateway.salvar(new Cliente("J", "12ABC34501DE35", "Top Motors Comércio LTDA", "Top Motors", "789123456", "contato@topmotors.com", "47999990010"));
        log.info("Clientes criados com sucesso.");

        var cliente = new Cliente("F", "00018563082", "João Silva", null, null, "joao.silva@email.com", "47999990001");
        var end = new Endereco("88385000", "Av. Nereu Ramos", "1000", "Sala 01", "Centro", "Penha", "SC");
        cliente.adicionarEndereco(end);
        return clienteGateway.salvar(cliente).getId();
    }

    private Long popularVeiculos() {
        var veiculo = new Veiculo("FOC2012", "Ford", "Focus Hatch Titanium", 2012, 2013, "Prata", "9BF123", "2.0 Duratec");
        return veiculoGateway.salvar(veiculo).getId();
    }

    private Long popularProdutos() {

        CriarProdutoCommand criarProdutoCommandSemSaldoInicial = new CriarProdutoCommand(
                "Bico Injetor Cleaner",
                TipoProduto.INSUMO,
                "Car80",
                "C80",
                "Universal",
                null,
                BigDecimal.valueOf(15.00),
                BigDecimal.valueOf(35)
        );
        criarProdutoUseCase.executar(criarProdutoCommandSemSaldoInicial);

        CriarProdutoCommand criarProdutoCommand = new CriarProdutoCommand(
                "Filtro de Óleo",
                TipoProduto.PECA,
                "Fram",
                "PH10060",
                "Ford Focus",
                BigDecimal.valueOf(45.00),
                BigDecimal.valueOf(70.00),
                BigDecimal.valueOf(50)
        );

        return criarProdutoUseCase.executar(criarProdutoCommand).id();
    }

    private Long popularServicos() {
        var servico = new Servico("Mão de obra - troca de filtro de óleo", BigDecimal.valueOf(40.0), 20);
        return servicoGateway.salvar(servico).getId();
    }

    private void popularOrdensServico(Long clienteId, Long veiculoId, Long produtoId, Long servicoId) {
        // OS recém-aberta (Status: RECEBIDA)
        var osAberta = new AbrirOrdemServicoCommand(clienteId, veiculoId, 45000, "Carro puxando para o lado");
        abrirOrdemServicoUseCase.executar(osAberta);

        /*
        // Ordem de Serviço aberta (Para testar o relatório de médias) ===
        var osConcluidaCmd = new AbrirOrdemServicoCommand(clienteId, veiculoId, 45000, "Revisão preventiva de rotina.");
        Long osConcluidaId = abrirOrdemServicoUseCase.executar(osConcluidaCmd).id();

        // Adiciona Item de Serviço (Passando os horários simulados para gerar métricas de tempo)
        // Simulando que iniciou às 09:00 e terminou às 09:25 (25 minutos executados vs 30 minutos padrão)
        var itemServicoCmd = new AdicionarItemServicoCommand(
                osConcluidaId, servicoId, null,
                LocalDate.now().atTime(9, 0), LocalDate.now().atTime(9, 25)
        );
        adicionarItemServicoUseCase.executar(itemServicoCmd);

        // Adiciona Item de Produto (Isso disparará automaticamente a baixa e a movimentação no estoque)
        var itemProdutoCmd = new AdicionarItemProdutoCommand(osConcluidaId, produtoId, BigDecimal.valueOf(1));
        adicionarItemProdutoUseCase.executar(itemProdutoCmd);

        // Conclui a OS mudando o status final
        concluirOrdemServicoUseCase.executar(new ConcluirOrdemServicoCommand(osConcluidaId));


        // === CENÁRIO 2: Ordem de Serviço EM ANDAMENTO (Para testar filtros e telas operacionais) ===
        var osAndamentoCmd = new AbrirOrdemServicoCommand(clienteId, veiculoId, "Verificação de infiltração d'água.");
        Long osAndamentoId = abrirOrdemServicoUseCase.executar(osAndamentoCmd).id();

        // Adiciona apenas o serviço iniciado, sem data de fim ainda
        var itemAndamentoCmd = new AdicionarItemServicoCommand(
                osAndamentoId, servicoId, mecanicoId,
                LocalDate.now().atTime(14, 0), null
        );
        adicionarItemServicoUseCase.executar(itemAndamentoCmd);

 */
    }

}
