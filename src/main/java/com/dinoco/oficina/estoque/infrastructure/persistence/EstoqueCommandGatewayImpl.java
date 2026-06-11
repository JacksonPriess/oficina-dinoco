package com.dinoco.oficina.estoque.infrastructure.persistence;

import com.dinoco.oficina.estoque.application.gateways.EstoqueCommandGateway;
import com.dinoco.oficina.estoque.domain.MovimentacaoEstoque;
import com.dinoco.oficina.estoque.domain.SaldoEstoque;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class EstoqueCommandGatewayImpl implements EstoqueCommandGateway {

    private final SaldoEstoqueJpaRepository saldoRepository;
    private final MovimentacaoEstoqueJpaRepository movimentacaoRepository;

    public EstoqueCommandGatewayImpl(
            SaldoEstoqueJpaRepository saldoRepository,
            MovimentacaoEstoqueJpaRepository movimentacaoRepository) {
        this.saldoRepository = saldoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Override
    public Optional<SaldoEstoque> buscarSaldoPorProdutoIdParaAlteracao(Long produtoId) {
        return saldoRepository.findByProdutoId(produtoId)
                .map(this::mapearParaDominio);
    }

    /**
     * O @Transactional garante que Saldo e Movimentação sejam salvos na mesma "viagem" ao banco.
     * Se um falhar, o outro é desfeito.
     */
    @Override
    @Transactional
    public void salvar(SaldoEstoque saldo, MovimentacaoEstoque movimentacao) {

        // 1. Salva o Saldo usando saveAndFlush para garantir o incremento imediato do @Version
        SaldoEstoqueEntity saldoEntity = mapearParaEntity(saldo);
        saldoRepository.saveAndFlush(saldoEntity);

        // 2. A Movimentação é opcional (lembre-se do ProdutoCadastradoListener que passa null)
        if (movimentacao != null) {
            MovimentacaoEstoqueEntity movEntity = mapearParaEntity(movimentacao);
            movimentacaoRepository.save(movEntity);
        }
    }

    // --- MÉTODOS DE MAPEAMENTO (DE/PARA) ---

    private SaldoEstoque mapearParaDominio(SaldoEstoqueEntity entity) {
        return new SaldoEstoque(
                entity.getId(),
                entity.getProdutoId(),
                entity.getQuantidadeReal(),
                entity.getQuantidadeReservada(),
                entity.getVersao()
        );
    }

    private SaldoEstoqueEntity mapearParaEntity(SaldoEstoque dominio) {
        SaldoEstoqueEntity entity = new SaldoEstoqueEntity();
        // O Id pode ser nulo se for uma prateleira nova recém-criada pelo evento
        // Nesse caso, só não podemos chamar setId(null) se usarmos reflexão, mas no set comum é tranquilo.
        // É importante checar e preservar a arquitetura
        entity.setProdutoId(dominio.getProdutoId());
        entity.setQuantidadeReal(dominio.getQuantidadeReal());
        entity.setQuantidadeReservada(dominio.getQuantidadeReservada());
        entity.setVersao(dominio.getVersao());

        // Use reflexão ou um setter protegido se o ID e versão estiverem privados no construtor
        // O Spring Data usa o ID não-nulo para saber que é um UPDATE (merge)
        return entity;
    }

    private MovimentacaoEstoqueEntity mapearParaEntity(MovimentacaoEstoque dominio) {
        MovimentacaoEstoqueEntity entity = new MovimentacaoEstoqueEntity();
        entity.setProdutoId(dominio.getProdutoId());
        entity.setTipoMovimentacao(dominio.getTipo());
        entity.setQuantidade(dominio.getQuantidade());
        entity.setDataMovimentacao(dominio.getDataMovimentacao());
        entity.setObservacao(dominio.getObservacao());
        return entity;
    }
}