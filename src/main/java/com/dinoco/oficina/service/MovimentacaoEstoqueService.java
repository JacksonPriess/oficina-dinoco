package com.dinoco.oficina.service;

import com.dinoco.oficina.entity.MovimentacaoEstoque;
import com.dinoco.oficina.entity.Produto;
import com.dinoco.oficina.enums.TipoMovimentacao;
import com.dinoco.oficina.repository.MovimentacaoEstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    public void registrarMovimentacao(Produto p, BigDecimal qtd, TipoMovimentacao tipo, String obs) {
        MovimentacaoEstoque mov = new MovimentacaoEstoque();
        mov.setProduto(p);
        mov.setQuantidade(qtd);
        mov.setTipoMovimentacao(tipo);
        mov.setObservacao(obs);
        movimentacaoRepository.save(mov);
    }
}
