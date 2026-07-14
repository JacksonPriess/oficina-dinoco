package com.dinoco.oficina.cliente.infrastructure.web.mapper;

import com.dinoco.oficina.cliente.application.usecases.commands.atualizar.AtualizarClienteCommand;
import com.dinoco.oficina.cliente.application.usecases.commands.atualizar.AtualizarClienteOutput;
import com.dinoco.oficina.cliente.application.usecases.commands.criar.CriarClienteCommand;
import com.dinoco.oficina.cliente.application.usecases.commands.criar.CriarClienteOutput;
import com.dinoco.oficina.cliente.application.usecases.commands.desativar.DesativarClienteCommand;
import com.dinoco.oficina.cliente.application.usecases.queries.buscarporid.BuscarClientePorIdOutput;
import com.dinoco.oficina.cliente.infrastructure.web.dto.ClienteRequestDto;
import com.dinoco.oficina.cliente.infrastructure.web.dto.ClienteResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClienteWebMapper {

    //Converte do DTO da Web para o Input do Core
    CriarClienteCommand toInput(ClienteRequestDto dto);

    //Converte do Output do Core para o DTO da Web
    ClienteResponseDto toResponse(CriarClienteOutput output);

    //Converte do DTO da Web para o Input do Core
    AtualizarClienteCommand toAtualizarCommand(Long id, ClienteRequestDto dto);

    //Converte do Output do Core para o DTO da Web
    ClienteResponseDto toAtualizarResponse(AtualizarClienteOutput output);

    DesativarClienteCommand toDesativarCommand(Long id);

    ClienteResponseDto toQueryResponse(BuscarClientePorIdOutput output);
}