package com.dinoco.oficina.controller;

import com.dinoco.oficina.dto.ProdutoRequestDto;
import com.dinoco.oficina.dto.ProdutoResponseDto;
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

    private final ProdutoService produtoService;

    @Operation(summary = "Incluir produto")
    @PostMapping
    public ResponseEntity<ProdutoResponseDto> cadastrar(@RequestBody @Valid ProdutoRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.criar(dto));
    }

    @Operation(summary = "Buscar produto por termo")
    @GetMapping
    public ResponseEntity<List<ProdutoResponseDto>> listar(
            @RequestParam(value = "busca", required = false) String termo) {
        return ResponseEntity.ok(produtoService.listar(termo));
    }

}
