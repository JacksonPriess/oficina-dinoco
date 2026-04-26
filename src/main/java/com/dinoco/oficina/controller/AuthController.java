package com.dinoco.oficina.controller;

import com.dinoco.oficina.infra.security.TokenService;
import com.dinoco.oficina.dto.LoginDto;
import com.dinoco.oficina.dto.TokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "1. Autenticação", description = "Endpoints de login e segurança")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @Operation(summary = "Realizar login e gerar token")
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var token = tokenService.gerarToken(auth.getName());
        return ResponseEntity.ok(new TokenDto(token));
    }
}
