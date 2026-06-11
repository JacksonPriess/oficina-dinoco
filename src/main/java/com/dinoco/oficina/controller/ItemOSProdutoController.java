package com.dinoco.oficina.controller;
/*
import com.dinoco.oficina.dto.ItemOSProdutoAdicionarDto;
import com.dinoco.oficina.aordemservico.infrastructure.web.dto.ItemOSProdutoAlterarDto;
import com.dinoco.oficina.dto.OrdemServicoDetalhadaResponseDto;
import com.dinoco.oficina.service.ItemOSProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "8. Itens da OS - Produtos", description = "Cadastro de itens de produto da OS")
@RestController
@RequiredArgsConstructor
public class ItemOSProdutoController {

    private final ItemOSProdutoService itemProdutoService;

    @Operation(summary = "Adicionar item")
    @PostMapping("api/ordens-servico/{osId}/produtos")
    public ResponseEntity<OrdemServicoDetalhadaResponseDto> adicionarProduto(@PathVariable Long osId, @Valid @RequestBody ItemOSProdutoAdicionarDto dto) {
        OrdemServicoDetalhadaResponseDto response = itemProdutoService.adicionarItemProduto(osId, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Alterar item")
    @PutMapping("api/itens-produto/{itemId}")
    public ResponseEntity<Void> alterarProduto(@PathVariable Long itemId, @Valid @RequestBody ItemOSProdutoAlterarDto dto) {
        itemProdutoService.alterarItemProduto(itemId, dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remover item")
    @DeleteMapping("api/itens-produto/{itemId}")
    public ResponseEntity<Void> removerProduto(@PathVariable Long itemId) {
        itemProdutoService.removerItemProduto(itemId);
        return ResponseEntity.noContent().build();
    }


}

 */