package com.dinoco.oficina.controller;

import com.dinoco.oficina.dto.VeiculoRequestDto;
import com.dinoco.oficina.dto.VeiculoResponseDto;
import com.dinoco.oficina.service.VeiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Veículos", description = "Cadastro de veículo")
@RestController
@RequestMapping("/api/veiculos")
@RequiredArgsConstructor
public class VeiculoController {

    private final VeiculoService service;

    @Operation(summary = "Cadastrar veículo")
    @PostMapping
    public ResponseEntity<VeiculoResponseDto> criar(@Valid @RequestBody VeiculoRequestDto request) {
        VeiculoResponseDto response = service.criar(request);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @Operation(summary = "Buscar veículo por código")
    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Atualizar veículo")
    @PutMapping("/{id}")
    public ResponseEntity<VeiculoResponseDto> atualizar(@PathVariable Long id, @RequestBody @Valid VeiculoRequestDto request) {
        VeiculoResponseDto response = service.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Desativar veículo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.noContent().build();
    }

}
