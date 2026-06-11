package com.dinoco.oficina.estoque.infrastructure.web.mapper;

import com.dinoco.oficina.estoque.application.usecases.commands.ajustarinventario.AjustarInventarioCommand;
import com.dinoco.oficina.estoque.infrastructure.web.dto.SaldoEstoqueRequestDto;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EstoqueWebMapper {

    AjustarInventarioCommand toInput(@Valid SaldoEstoqueRequestDto request);

}
