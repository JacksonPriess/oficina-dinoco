package com.dinoco.oficina.controller;

import com.dinoco.oficina.dto.ItemOSServicoAdicionarDto;
import com.dinoco.oficina.dto.ItemOSServicoAlterarDto;
import com.dinoco.oficina.dto.OrdemServicoDetalhadaResponseDto;
import com.dinoco.oficina.service.ItemOSServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "9. Itens da OS - Serviços", description = "Cadastro de itens de serviços da OS")
@RestController
@RequiredArgsConstructor
public class ItemOSServicoController {

    private final ItemOSServicoService itemOSServicoService;

    @Operation(summary = "Adicionar item")
    @PostMapping("api/ordens-servico/{osId}/servicos")
    public ResponseEntity<OrdemServicoDetalhadaResponseDto> adicionarServico(@PathVariable Long osId, @Valid @RequestBody ItemOSServicoAdicionarDto dto) {
        OrdemServicoDetalhadaResponseDto response = itemOSServicoService.adicionarItemServico(osId, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Alterar item")
    @PutMapping("api/itens-servico/{itemId}")
    public ResponseEntity<Void> alterarServico(@PathVariable Long itemId, @Valid @RequestBody ItemOSServicoAlterarDto dto) {
        itemOSServicoService.alterarItemServico(itemId, dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remover item")
    @DeleteMapping("api/itens-servico/{itemId}")
    public ResponseEntity<Void> removerServico(@PathVariable Long itemId) {
        itemOSServicoService.removerItemServico(itemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Iniciar execução")
    @PostMapping("api/itens-servico/{itemId}/iniciar-execucao")
    public ResponseEntity<Void> iniciarExecucaoServico(@PathVariable Long itemId) {
        itemOSServicoService.iniciarExecucaoItemServico(itemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Concluir execução")
    @PostMapping("api/itens-servico/{itemId}/concluir-execucao")
    public ResponseEntity<Void> concluirExecucaoServico(@PathVariable Long itemId) {
        itemOSServicoService.concluirExecucaoItemServico(itemId);
        return ResponseEntity.noContent().build();
    }

}