package com.dinoco.oficina.funcionario.infrastructure.web.mapper;

import com.dinoco.oficina.funcionario.application.usecases.commands.atualizar.AtualizarFuncionarioCommand;
import com.dinoco.oficina.funcionario.application.usecases.commands.atualizar.AtualizarFuncionarioOutput;
import com.dinoco.oficina.funcionario.application.usecases.commands.criar.CriarFuncionarioCommand;
import com.dinoco.oficina.funcionario.application.usecases.commands.criar.CriarFuncionarioOutput;
import com.dinoco.oficina.funcionario.application.usecases.commands.desativar.DesativarFuncionarioCommand;
import com.dinoco.oficina.funcionario.application.usecases.commands.resetarsenha.ResetarSenhaFuncionarioCommand;
import com.dinoco.oficina.funcionario.application.usecases.commands.resetarsenha.ResetarSenhaFuncionarioOutput;
import com.dinoco.oficina.funcionario.application.usecases.queries.buscarporid.BuscarFuncionarioPorIdOutput;
import com.dinoco.oficina.funcionario.infrastructure.web.dto.FuncionarioRequestDto;
import com.dinoco.oficina.funcionario.infrastructure.web.dto.FuncionarioResponseDto;
import com.dinoco.oficina.funcionario.infrastructure.web.dto.SenhaResetadaResponseDto;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FuncionarioWebMapper {
    CriarFuncionarioCommand toInputCriar(@Valid FuncionarioRequestDto request);

    FuncionarioResponseDto toResponseCriar(CriarFuncionarioOutput output);

    ResetarSenhaFuncionarioCommand toResetarSenha(Long id);

    SenhaResetadaResponseDto toResponseSenhaResetada(ResetarSenhaFuncionarioOutput output);

    AtualizarFuncionarioCommand toInputAtualizar(Long id, @Valid FuncionarioRequestDto request);

    FuncionarioResponseDto toResponseAtualizar(AtualizarFuncionarioOutput output);

    DesativarFuncionarioCommand toDesativarCommand(Long id);

    FuncionarioResponseDto toQueryResponse(BuscarFuncionarioPorIdOutput output);
}
