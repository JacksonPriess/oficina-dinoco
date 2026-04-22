package com.dinoco.oficina.controller;

import com.dinoco.oficina.dto.ItemServicoAdicionarDto;
import com.dinoco.oficina.dto.ItemServicoAlterarDto;
import com.dinoco.oficina.service.ItemOSServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Itens da OS - Serviços", description = "Cadastro de itens de serviços da OS")
@RestController
@RequiredArgsConstructor
public class ItemOSServicoController {

    private final ItemOSServicoService itemOSServicoService;

    @Operation(summary = "Adicionar item")
    @PostMapping("/api/ordens-servico/{osId}/servicos")
    public ResponseEntity<Void> adicionarServico(@PathVariable Long osId, @RequestBody ItemServicoAdicionarDto dto) {
        itemOSServicoService.adicionarItemServico(osId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Alterar item")
    @PutMapping("/api/itens-servico/{itemId}")
    public ResponseEntity<Void> alterarServico(@PathVariable Long itemId, @RequestBody ItemServicoAlterarDto dto) {
        itemOSServicoService.alterarItemServico(itemId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remover item")
    @DeleteMapping("/api/itens-servico/{itemId}")
    public ResponseEntity<Void> removerServico(@PathVariable Long itemId) {
        itemOSServicoService.removerItemServico(itemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Iniciar execução")
    @PostMapping("/api/itens-servico/{itemId}/iniciar-execucao")
    public ResponseEntity<Void> iniciarExecucaoServico(@PathVariable Long itemId) {
        itemOSServicoService.iniciarExecucaoItemServico(itemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Concluir execução")
    @PostMapping("/api/itens-servico/{itemId}/concluir-execucao")
    public ResponseEntity<Void> concluirExecucaoServico(@PathVariable Long itemId) {
        itemOSServicoService.concluirExecucaoItemServico(itemId);
        return ResponseEntity.noContent().build();
    }

}