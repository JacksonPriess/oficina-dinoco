package com.dinoco.oficina.controller;

import com.dinoco.oficina.dto.TrocarSenhaRequestDto;
import com.dinoco.oficina.entity.Usuario;
import com.dinoco.oficina.infra.security.TokenService;
import com.dinoco.oficina.dto.LoginDto;
import com.dinoco.oficina.dto.TokenDto;
import com.dinoco.oficina.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1. Autenticação", description = "Endpoints de login e segurança")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Operation(summary = "Realizar login e gerar token")
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var token = tokenService.gerarToken(auth.getName());
        return ResponseEntity.ok(new TokenDto(token));
    }
    @Operation(summary = "Trocar senha")
    @PutMapping("/trocar-senha")
    public ResponseEntity<Void> trocarSenha(@RequestBody @Valid TrocarSenhaRequestDto dto, @AuthenticationPrincipal Usuario usuarioLogado) {
        usuarioService.registrarNovaSenha(usuarioLogado.getId(), dto.novaSenha());
        return ResponseEntity.noContent().build();
    }
}
