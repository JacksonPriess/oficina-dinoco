package com.dinoco.oficina.controller;

import com.dinoco.oficina.dto.*;
import com.dinoco.oficina.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Produtos", description = "Controle de estoque")
@RestController
@RequestMapping("api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService service;

    @Operation(summary = "Incluir produto")
    @PostMapping
    public ResponseEntity<ProdutoResponseDto> cadastrar(@RequestBody @Valid ProdutoRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @Operation(summary = "Buscar produto por termo")
    @GetMapping
    public ResponseEntity<List<ProdutoResponseDto>> listar(
            @RequestParam(value = "busca", required = false) String termo) {
        return ResponseEntity.ok(service.listar(termo));
    }

    @Operation(summary = "Atualizar produto")
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDto> atualizar(@PathVariable Long id, @RequestBody @Valid ProdutoUpdateRequestDto request) {
        ProdutoResponseDto response = service.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Desativar produto")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.noContent().build();
    }

}
