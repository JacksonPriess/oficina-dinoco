package com.dinoco.oficina.controller;

import com.dinoco.oficina.dto.*;
import com.dinoco.oficina.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@Tag(name = "5. Produtos", description = "Controle de produto/estoque")
@RestController
@RequestMapping("api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService service;

    @Operation(summary = "Incluir produto")
    @PostMapping
    public ResponseEntity<ProdutoResponseDto> cadastrar(@RequestBody @Valid ProdutoRequestDto request) {
        ProdutoResponseDto response = service.criar(request);

        // Retorna 201 Created com a URL do novo recurso no Header Location
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @Operation(summary = "Buscar produto por termo")
    @GetMapping
    public ResponseEntity<List<ProdutoResponseDto>> listar(@RequestParam(value = "busca", required = false) String termo) {
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
