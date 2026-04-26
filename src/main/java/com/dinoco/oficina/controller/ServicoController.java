package com.dinoco.oficina.controller;

import com.dinoco.oficina.dto.ServicoRequestDto;
import com.dinoco.oficina.dto.ServicoResponseDto;
import com.dinoco.oficina.service.ServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@Tag(name = "6. Serviços", description = "Cadastro de serviço")
@RestController
@RequestMapping("/api/servicos")
@RequiredArgsConstructor
public class ServicoController {

    private final ServicoService service;

    @Operation(summary = "Cadastrar serviço")
    @PostMapping
    public ResponseEntity<ServicoResponseDto> criar(@RequestBody @Valid ServicoRequestDto request) {
        ServicoResponseDto response = service.criar(request);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @Operation(summary = "Buscar serviço por código")
    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Atualizar serviço")
    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponseDto> atualizar(@PathVariable Long id, @RequestBody @Valid ServicoRequestDto request) {
        ServicoResponseDto response = service.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Desativar serviço")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.noContent().build();
    }
}