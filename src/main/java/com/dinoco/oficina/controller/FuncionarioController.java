package com.dinoco.oficina.controller;

import com.dinoco.oficina.dto.FuncionarioRequestDto;
import com.dinoco.oficina.dto.FuncionarioResponseDto;
import com.dinoco.oficina.dto.SenhaResetadaResponseDto;
import com.dinoco.oficina.service.FuncionarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@Tag(name = "3. Funcionários", description = "Cadastro de funcionário")
@RestController
@RequestMapping("api/funcionarios")
@RequiredArgsConstructor
public class FuncionarioController {

    private final FuncionarioService service;

    @Operation(summary = "Cadastrar funcionário")
    @PostMapping
    public ResponseEntity<FuncionarioResponseDto> criar(@RequestBody @Valid FuncionarioRequestDto request) {
        FuncionarioResponseDto response = service.criar(request);

        // Retorna 201 Created com a URL do novo recurso no Header Location
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @Operation(summary = "Buscar funcionário por código")
    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Atualizar funcionário")
    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDto> atualizar(@PathVariable Long id, @RequestBody @Valid FuncionarioRequestDto request) {
        FuncionarioResponseDto response = service.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Desativar funcionário")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Resetar senha funcionário - Gerar um novo acesso temporário.")
    @PutMapping("/{id}/reset-senha")
    public ResponseEntity<SenhaResetadaResponseDto> resetarSenha(@PathVariable Long id) {
        String senhaTemporaria = service.resetarSenhaFuncionario(id);
        return ResponseEntity.ok(new SenhaResetadaResponseDto(senhaTemporaria));
    }

}
