package com.dinoco.oficina.ordemservico.infrastructure.web;

import com.dinoco.oficina.ordemservico.adapters.controllers.ItemOSProdutoControllerClean;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemproduto.AdicionarItemProdutoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemproduto.AlterarItemProdutoCommand;
import com.dinoco.oficina.ordemservico.infrastructure.web.dto.ItemOSProdutoAdicionarDto;
import com.dinoco.oficina.ordemservico.infrastructure.web.mapper.OrdemServicoWebMapper;
import com.dinoco.oficina.ordemservico.infrastructure.web.dto.ItemOSProdutoAlterarDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Itens da OS - Produtos", description = "Cadastro de itens de produto da OS")
@RestController
@RequestMapping("api/ordens-servico/{osId}/produtos")
public class ItemOSProdutoSpringWebController {

    private final ItemOSProdutoControllerClean controllerClean;
    private final OrdemServicoWebMapper mapper;

    public ItemOSProdutoSpringWebController(ItemOSProdutoControllerClean controllerClean, OrdemServicoWebMapper mapper) {
        this.controllerClean = controllerClean;
        this.mapper = mapper;
    }

    @Operation(summary = "Adicionar item de produto")
    @PostMapping
    public ResponseEntity<Void> adicionarProduto(@PathVariable Long osId, @Valid @RequestBody ItemOSProdutoAdicionarDto request) {
        AdicionarItemProdutoCommand input = mapper.toAdicionarItemProdutoCommand(osId, request);
        controllerClean.adicionarItemProduto(input);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Alterar item de produto")
    @PutMapping("/{itemId}")
    public ResponseEntity<Void> alterarProduto(@PathVariable Long osId, @PathVariable Long itemId, @Valid @RequestBody ItemOSProdutoAlterarDto dto) {
        AlterarItemProdutoCommand input = mapper.toAlterarItemProdutoCommand(osId, itemId, dto);
        controllerClean.alterarItemProduto(input);
        return ResponseEntity.noContent().build();
    }

/*
    @Operation(summary = "Remover item")
    @DeleteMapping("api/itens-produto/{itemId}")
    public ResponseEntity<Void> removerProduto(@PathVariable Long itemId) {
        itemProdutoService.removerItemProduto(itemId);
        return ResponseEntity.noContent().build();
    }

     */


}