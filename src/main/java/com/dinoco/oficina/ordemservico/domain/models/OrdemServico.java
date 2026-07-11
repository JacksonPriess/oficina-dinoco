package com.dinoco.oficina.ordemservico.domain.models;

import com.dinoco.oficina.ordemservico.domain.enums.StatusItemServico;
import com.dinoco.oficina.ordemservico.domain.enums.StatusOS;
import com.dinoco.oficina.ordemservico.domain.exceptions.RegraNegocioOSException;
import com.dinoco.oficina.ordemservico.domain.exceptions.TransicaoStatusInvalidaException;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class OrdemServico {

    private Long id;
    private String codigoRastreio;
    private Long clienteId;
    private Long veiculoId;
    private StatusOS status;
    private String reclamacaoCliente;
    private String laudoTecnico;
    private Integer quilometragemEntrada;

    private BigDecimal valorTotalServicos;
    private BigDecimal valorTotalProdutos;
    private BigDecimal valorDesconto;
    private BigDecimal valorTotalOS;

    private LocalDateTime dataEntrada;
    private LocalDateTime dataSaida;
    private LocalDateTime dataReprovacao;

    private List<ItemOSServico> itensServico;
    private List<ItemOSProduto> itensProduto;

    public OrdemServico(Long clienteId, Long veiculoId, Integer quilometragem, String reclamacao) {
        this.codigoRastreio = "OS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.clienteId = clienteId;
        this.veiculoId = veiculoId;
        this.quilometragemEntrada = quilometragem;
        this.reclamacaoCliente = reclamacao;
        this.status = StatusOS.RECEBIDA;
        this.itensServico = new ArrayList<>();
        this.itensProduto = new ArrayList<>();
        this.valorTotalServicos = BigDecimal.ZERO;
        this.valorTotalProdutos = BigDecimal.ZERO;
        this.valorDesconto = BigDecimal.ZERO;
        this.valorTotalOS = BigDecimal.ZERO;
        this.dataEntrada = LocalDateTime.now();
        recalcularTotais();
    }

    public OrdemServico(Long id, String codigoRastreio, Long clienteId, Long veiculoId, StatusOS status,
                        String reclamacaoCliente, String laudoTecnico, Integer quilometragemEntrada, BigDecimal valorDesconto,
                        BigDecimal valorTotalServicos, BigDecimal valorTotalProdutos, BigDecimal valorTotalOS, LocalDateTime dataEntrada, LocalDateTime dataSaida, LocalDateTime dataReprovacao) {
        this.id = id;
        this.codigoRastreio = codigoRastreio;
        this.clienteId = clienteId;
        this.veiculoId = veiculoId;
        this.status = status;
        this.quilometragemEntrada = quilometragemEntrada;
        this.reclamacaoCliente = reclamacaoCliente;
        this.laudoTecnico = laudoTecnico;
        this.valorDesconto = valorDesconto;
        this.valorTotalServicos = valorTotalServicos;
        this.valorTotalProdutos = valorTotalProdutos;
        this.valorTotalOS = valorTotalOS;
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
        this.dataReprovacao = dataReprovacao;
        this.itensServico = new ArrayList<>();
        this.itensProduto = new ArrayList<>();
    }

    public void iniciarDiagnostico() {
        if (this.status != StatusOS.RECEBIDA) {
            throw new TransicaoStatusInvalidaException("Para iniciar o diagnóstico, a OS deve estar RECEBIDA.");
        }
        this.status = StatusOS.EM_DIAGNOSTICO;
    }

    public void adicionarProduto(ItemOSProduto novoProduto) {

        if (this.status != StatusOS.EM_DIAGNOSTICO && this.status != StatusOS.AGUARDANDO_ORCAMENTO) {
            throw new RegraNegocioOSException("A OS não permite adição de produtos no status atual: " + this.status);
        }

        if (this.itensProduto == null) {
            this.itensProduto = new ArrayList<>();
        }

        boolean produtoJaExiste = this.itensProduto.stream()
                .anyMatch(item -> item.getProdutoId().equals(novoProduto.getProdutoId()));

        if (produtoJaExiste) {
            throw new RegraNegocioOSException("Este produto já está na OS. Por favor, edite a quantidade do item existente.");
        }

        this.itensProduto.add(novoProduto);
        this.recalcularTotais();
    }

    public void adicionarServico(ItemOSServico novoServico) {
        if (this.status != StatusOS.EM_DIAGNOSTICO && this.status != StatusOS.AGUARDANDO_ORCAMENTO) {
            throw new RegraNegocioOSException("A OS não permite adição de serviços no status atual: " + this.status);
        }

        if (this.itensServico == null) {
            this.itensServico = new ArrayList<>();
        }

        boolean servicoJaExiste = this.itensServico.stream()
                .anyMatch(item -> item.getServicoId().equals(novoServico.getServicoId()));

        if (servicoJaExiste) {
            throw new RegraNegocioOSException("Este serviço já está na OS.");
        }

        this.itensServico.add(novoServico);
        this.recalcularTotais();
    }

    public void concluirDiagnostico(String laudoTecnico) {
        if (this.status != StatusOS.EM_DIAGNOSTICO) {
            throw new TransicaoStatusInvalidaException("A OS precisa estar EM_DIAGNOSTICO para ser concluída.");
        }
        if (this.itensServico.isEmpty()) {
            throw new RegraNegocioOSException("Não é possível concluir diagnóstico sem apontar ao menos um serviço.");
        }
        this.laudoTecnico = laudoTecnico;
        this.status = StatusOS.AGUARDANDO_ORCAMENTO;
    }

    /**
     * Para que o envio do orçamento seja possível, é necessário que a OS tenha
     * ao menos um serviço com valor e ao menos um produto com valor.
     */
    public void enviarOrcamento() {

        boolean temItemProdutoSemPreco = this.getItensProduto().stream()
                .anyMatch(item -> item.getValorUnitarioVenda().compareTo(BigDecimal.ZERO) <= 0);

        boolean temItemServicoSemValor = this.getItensServico().stream()
                .anyMatch(item -> item.getValorCobrado().compareTo(BigDecimal.ZERO) <= 0);

        // Na prática, se o valor de serviço estiver zerado, a equipe da Oficina informará um valor devido para o item.
        // E caso algum produto esteja sem valor no item da OS, é porque não se sabe o preço do produto, então será necessário realizar uma cotação com fornecedor
        // e após obter o preço de custo, o usuário vai atualizar o preco unitário do item da OS, para que o orçamento possa ser enviado.
        if ( temItemProdutoSemPreco ) {
            throw new RegraNegocioOSException("Existem itens de produto da OS sem valor unitário de venda R$.");
        }
        if ( temItemServicoSemValor ) {
            throw new RegraNegocioOSException("Existem itens de serviço da OS sem valor da mão de obra R$.");
        }

        this.status = StatusOS.AGUARDANDO_APROVACAO;
    }

    public void alterarItemServico(Long itemId, BigDecimal novoValorCobrado, Long novoMecanicoId) {
        if (this.status != StatusOS.EM_DIAGNOSTICO && this.status != StatusOS.AGUARDANDO_ORCAMENTO) {
            throw new RegraNegocioOSException("A OS não permite alteração de serviços no status atual: " + this.status);
        }

        ItemOSServico itemOSServico = this.itensServico.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RegraNegocioOSException("Item de serviço não encontrado nesta OS."));

        itemOSServico.alterarDados(novoValorCobrado, novoMecanicoId);
        this.recalcularTotais();
    }

    public void iniciarExecucaoItemServico(Long itemId) {
        if (this.status != StatusOS.EM_EXECUCAO) {
            throw new RegraNegocioOSException("A OS não permite alteração de serviços no status atual: " + this.status);
        }

        ItemOSServico itemOSServico = this.itensServico.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RegraNegocioOSException("Item de serviço não encontrado nesta OS."));

        itemOSServico.iniciarExecucao();
    }

    public void concluirExecucaoItemServico(Long itemId, LocalDateTime dataHoraFim) {
        if (this.status != StatusOS.EM_EXECUCAO) {
            throw new RegraNegocioOSException("A OS não permite alteração de serviços no status atual: " + this.status);
        }

        ItemOSServico itemOSServico = this.itensServico.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RegraNegocioOSException("Item de serviço não encontrado nesta OS."));

        itemOSServico.concluirExecucao(dataHoraFim);
    }

    public void alterarItemProduto(Long itemId, BigDecimal novoPrecoVenda, BigDecimal novaQuantidade) {
        if (this.status != StatusOS.EM_DIAGNOSTICO && this.status != StatusOS.AGUARDANDO_ORCAMENTO) {
            throw new RegraNegocioOSException("A OS não permite alteração de produto no status atual: " + this.status);
        }

        ItemOSProduto itemOSProduto = this.itensProduto.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RegraNegocioOSException("Item de produto não encontrado nesta OS."));

        itemOSProduto.alterarDados(novoPrecoVenda, novaQuantidade);
        this.recalcularTotais();
    }

    public void reprovarOrcamento() {
        validarStatusAtual(StatusOS.AGUARDANDO_APROVACAO);
        this.status = StatusOS.REPROVADA;
        this.dataReprovacao = LocalDateTime.now();
    }

    public void marcarAguardandoFornecedor() {
        validarStatusAtual(StatusOS.AGUARDANDO_APROVACAO);
        this.status = StatusOS.AGUARDANDO_FORNECEDOR;
    }

    public void marcarProntaParaExecucao() {
        if (this.status != StatusOS.AGUARDANDO_APROVACAO && this.status != StatusOS.AGUARDANDO_FORNECEDOR) {
            throw new TransicaoStatusInvalidaException("Transição inválida para PRONTA_PARA_EXECUCAO.");
        }
        this.status = StatusOS.AGUARDANDO_EXECUCAO;
    }

    public void iniciarExecucao() {
        validarStatusAtual(StatusOS.AGUARDANDO_EXECUCAO);
        this.status = StatusOS.EM_EXECUCAO;
    }

    public void finalizarExecucao() {
        validarStatusAtual(StatusOS.EM_EXECUCAO);

        boolean temServicoPendente = itensServico.stream()
                .anyMatch(item -> item.getStatusItem() != StatusItemServico.CONCLUIDO);

        if ( temServicoPendente ) {
            throw new RegraNegocioOSException("Existem serviços pendentes. Conclua todos antes de finalizar a OS.");
        }
        this.status = StatusOS.FINALIZADA;
    }

    public void concluir() {
        validarStatusAtual(StatusOS.FINALIZADA);
        //TODO - Faturamento - Nota de Servico
        this.status = StatusOS.ENTREGUE;
        this.dataSaida = LocalDateTime.now();
    }

    public void recalcularTotais() {

        if (itensServico != null) {
            this.valorTotalServicos = itensServico.stream()
                    .map(ItemOSServico::getValorCobrado)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        if (itensProduto != null) {
            this.valorTotalProdutos = itensProduto.stream()
                    .map(ItemOSProduto::getValorTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        this.valorTotalOS = this.valorTotalServicos
                .add(this.valorTotalProdutos)
                .subtract(this.valorDesconto)
                .max(BigDecimal.ZERO);
    }

    private void validarStatusAtual(StatusOS esperado) {
        if (this.status != esperado) {
            throw new TransicaoStatusInvalidaException("Status inválido. Esperado: " + esperado + ", Atual: " + this.status);
        }
    }
}