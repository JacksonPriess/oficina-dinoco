package com.dinoco.oficina.controller;

import com.dinoco.oficina.dto.*;
import com.dinoco.oficina.entity.OrdemServico;
import com.dinoco.oficina.service.OrdemServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@Tag(name = "Ordem de servico", description = "Criação e acompanhamento da Ordem de Serviço")
@RestController
@RequestMapping("api/ordens-servico")
@RequiredArgsConstructor
public class OrdemServicoController {

    private final OrdemServicoService service;

    @Operation(summary = "Abrir cliente")
    @PostMapping
    public ResponseEntity<OrdemServicoResponseDto> abrirOS(@RequestBody @Valid OrdemServicoRequestDto dto) {
        OrdemServicoResponseDto response = service.abrirOs(dto);

        // Retorna 201 Created com a URL do novo recurso no Header Location
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @PostMapping("/{id}/produtos")
    public ResponseEntity<Void> adicionarProduto(@PathVariable Long id, @RequestBody @Valid ItemProdutoAdicionarDto dto) {
        service.adicionarProduto(id, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/servicos")
    public ResponseEntity<Void> adicionarServico(@PathVariable Long id, @RequestBody @Valid ItemServicoAdicionarDto dto) {
        service.adicionarServico(id, dto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> alterarStatus(@PathVariable Long id, @RequestBody @Valid AlterarStatusOsDto dto) {
        service.alterarStatus(id, dto.novoStatus());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServico> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarOuFalhar(id));
    }

}
