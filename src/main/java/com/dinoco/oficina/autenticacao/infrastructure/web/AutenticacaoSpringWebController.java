package com.dinoco.oficina.autenticacao.infrastructure.web;

import com.dinoco.oficina.autenticacao.adapters.controllers.AutenticacaoControllerClean;
import com.dinoco.oficina.autenticacao.application.usecases.realizarlogin.RealizarLoginCommand;
import com.dinoco.oficina.autenticacao.application.usecases.realizarlogin.RealizarLoginOutput;
import com.dinoco.oficina.autenticacao.application.usecases.trocarsenha.TrocarSenhaCommand;
import com.dinoco.oficina.autenticacao.infrastructure.persistence.UsuarioEntity;
import com.dinoco.oficina.autenticacao.infrastructure.web.dto.LoginDto;
import com.dinoco.oficina.autenticacao.infrastructure.web.dto.TokenDto;
import com.dinoco.oficina.autenticacao.infrastructure.web.dto.TrocarSenhaRequestDto;
import com.dinoco.oficina.autenticacao.infrastructure.web.mapper.AutenticacaoWebMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1. Autenticação", description = "Endpoints de login e segurança")
@RestController
@RequestMapping("api/auth")
public class AutenticacaoSpringWebController {

    private final AutenticacaoControllerClean controllerClean;
    private final AutenticacaoWebMapper mapper;

    public AutenticacaoSpringWebController(AutenticacaoControllerClean controllerClean, AutenticacaoWebMapper mapper) {
        this.controllerClean = controllerClean;
        this.mapper = mapper;
    }

    @Operation(summary = "Realizar login e gerar token")
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto request) {
        RealizarLoginCommand input = mapper.toInputRealizarLogin(request);
        RealizarLoginOutput output = controllerClean.realizarLogin(input);
        TokenDto response = mapper.toTokenDto(output);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Trocar senha")
    @PutMapping("/trocar-senha")
    public ResponseEntity<Void> trocarSenha(@RequestBody @Valid TrocarSenhaRequestDto dto, @AuthenticationPrincipal UsuarioEntity usuarioLogado) {
        var command = new TrocarSenhaCommand(usuarioLogado.getId(), dto.novaSenha());
        controllerClean.trocarSenha(command);
        return ResponseEntity.noContent().build();
    }
}
