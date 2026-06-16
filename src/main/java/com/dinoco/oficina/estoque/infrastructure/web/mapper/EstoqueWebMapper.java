package com.dinoco.oficina.estoque.infrastructure.web.mapper;

import com.dinoco.oficina.estoque.application.usecases.commands.ajustarinventario.AjustarInventarioCommand;
import com.dinoco.oficina.estoque.application.usecases.commands.registrarentrada.RegistrarEntradaCommand;
import com.dinoco.oficina.estoque.infrastructure.web.dto.EntradaSaldoEstoqueRequestDto;
import com.dinoco.oficina.estoque.infrastructure.web.dto.SaldoEstoqueRequestDto;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EstoqueWebMapper {

    AjustarInventarioCommand toInput(Long produtoId, @Valid SaldoEstoqueRequestDto request);

    RegistrarEntradaCommand toInputEntradaCompra(Long produtoId, @Valid EntradaSaldoEstoqueRequestDto request);
}
