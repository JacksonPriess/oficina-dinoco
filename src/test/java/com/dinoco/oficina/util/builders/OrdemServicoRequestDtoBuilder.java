package com.dinoco.oficina.util.builders;

import com.dinoco.oficina.ordemservico.infrastructure.web.dto.OrdemServicoRequestDto;

public class OrdemServicoRequestDtoBuilder {
    private Long clienteId = 1L;
    private Long veiculoId = 1L;
    private Integer quilometragemEntrada = 85000;
    private String reclamacaoCliente = "Barulho no motor";

    public static OrdemServicoRequestDtoBuilder umRequest() {
        return new OrdemServicoRequestDtoBuilder();
    }

    public OrdemServicoRequestDtoBuilder comClienteId(Long clienteId) {
        this.clienteId = clienteId;
        return this;
    }

    public OrdemServicoRequestDtoBuilder comQuilometragem(Integer quilometragem) {
        this.quilometragemEntrada = quilometragem;
        return this;
    }

    public OrdemServicoRequestDtoBuilder comReclamacao(String reclamacao) {
        this.reclamacaoCliente = reclamacao;
        return this;
    }

    public OrdemServicoRequestDto build() {
        return new OrdemServicoRequestDto(clienteId, veiculoId, quilometragemEntrada, reclamacaoCliente);
    }
}
