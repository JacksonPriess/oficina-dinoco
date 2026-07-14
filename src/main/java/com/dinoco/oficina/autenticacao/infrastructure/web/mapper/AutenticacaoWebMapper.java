package com.dinoco.oficina.autenticacao.infrastructure.web.mapper;

import com.dinoco.oficina.autenticacao.application.usecases.realizarlogin.RealizarLoginCommand;
import com.dinoco.oficina.autenticacao.application.usecases.realizarlogin.RealizarLoginOutput;
import com.dinoco.oficina.autenticacao.infrastructure.web.dto.LoginDto;
import com.dinoco.oficina.autenticacao.infrastructure.web.dto.TokenDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AutenticacaoWebMapper {

    RealizarLoginCommand toInputRealizarLogin(LoginDto request);

    TokenDto toTokenDto(RealizarLoginOutput output);
}