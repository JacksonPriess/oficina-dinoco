package com.dinoco.oficina.controller;

import com.dinoco.oficina.config.TokenService;
import com.dinoco.oficina.dto.LoginDto;
import com.dinoco.oficina.dto.TokenDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());

        // O Spring Security vai verificar se o usuário "admin" e senha "123456" batem
        var auth = this.authenticationManager.authenticate(usernamePassword);

        // Se bateu, geramos o token
        var token = tokenService.gerarToken(auth.getName());

        return ResponseEntity.ok(new TokenDto(token));
    }
}
