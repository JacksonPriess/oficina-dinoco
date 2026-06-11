package com.dinoco.oficina.catalogoproduto.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;
import java.math.BigDecimal;

@Entity
@Table(name = "view_produto_estoque")
@Immutable
@Data
public class ProdutoEstoqueEntity {

    @Id
    private Long id;
    private Long version;
    private String nome;
    private String tipo;
    private String marca;
    private String codigoFabricante;
    private String aplicacao;
    private BigDecimal precoCusto;
    private BigDecimal precoVenda;
    private Boolean ativo;
    private BigDecimal quantidadeReal;
    private BigDecimal quantidadeReservada;
}