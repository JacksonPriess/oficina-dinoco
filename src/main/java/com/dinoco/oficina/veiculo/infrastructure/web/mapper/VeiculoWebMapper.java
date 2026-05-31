package com.dinoco.oficina.veiculo.infrastructure.web.mapper;


import com.dinoco.oficina.veiculo.application.usecases.commands.atualizar.AtualizarVeiculoCommand;
import com.dinoco.oficina.veiculo.application.usecases.commands.atualizar.AtualizarVeiculoOutput;
import com.dinoco.oficina.veiculo.application.usecases.commands.criar.CriarVeiculoCommand;
import com.dinoco.oficina.veiculo.application.usecases.commands.criar.CriarVeiculoOutput;
import com.dinoco.oficina.veiculo.application.usecases.commands.desativar.DesativarVeiculoCommand;
import com.dinoco.oficina.veiculo.application.usecases.queries.buscarporid.BuscarVeiculoPorIdOutput;
import com.dinoco.oficina.veiculo.infrastructure.web.dto.VeiculoRequestDto;
import com.dinoco.oficina.veiculo.infrastructure.web.dto.VeiculoResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VeiculoWebMapper {

    //Converte do DTO da Web para o Input do Core
    CriarVeiculoCommand toInput(VeiculoRequestDto dto);

    //Converte do Output do Core para o DTO da Web
    VeiculoResponseDto toResponse(CriarVeiculoOutput output);

    //Converte do DTO da Web para o Input do Core
    AtualizarVeiculoCommand toAtualizarCommand(Long id, VeiculoRequestDto dto);

    //Converte do Output do Core para o DTO da Web
    VeiculoResponseDto toAtualizarResponse(AtualizarVeiculoOutput output);

    DesativarVeiculoCommand toDesativarCommand(Long id);

    VeiculoResponseDto toQueryResponse(BuscarVeiculoPorIdOutput output);
}