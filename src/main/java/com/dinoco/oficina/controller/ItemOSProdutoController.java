package com.dinoco.oficina.controller;

import com.dinoco.oficina.dto.ItemProdutoAdicionarDto;
import com.dinoco.oficina.dto.ItemProdutoAlterarDto;
import com.dinoco.oficina.service.ItemOSProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Itens da OS - Produtos", description = "Cadastro de itens de produto da OS")
@RestController
@RequiredArgsConstructor
public class ItemOSProdutoController {

    private final ItemOSProdutoService itemProdutoService;

    @Operation(summary = "Adicionar item")
    @PostMapping("/api/ordens-servico/{osId}/produtos")
    public ResponseEntity<Void> adicionarProduto(@PathVariable Long osId, @Valid @RequestBody ItemProdutoAdicionarDto dto) {
        itemProdutoService.adicionarItemProduto(osId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Alterar item")
    @PutMapping("/api/itens-produto/{itemId}")
    public ResponseEntity<Void> alterarProduto(@PathVariable Long itemId, @Valid @RequestBody ItemProdutoAlterarDto dto) {
        itemProdutoService.alterarItemProduto(itemId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remover item")
    @DeleteMapping("/api/itens-produto/{itemId}")
    public ResponseEntity<Void> removerProduto(@PathVariable Long itemId) {
        itemProdutoService.removerItemProduto(itemId);
        return ResponseEntity.noContent().build();
    }
}