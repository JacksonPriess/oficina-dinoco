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

    @Operation(summary = "Abrir ordem de serviço")
    @PostMapping
    public ResponseEntity<OrdemServicoResponseDto> abrirOS(@RequestBody @Valid OrdemServicoRequestDto dto) {
        OrdemServicoResponseDto response = service.abrirOs(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @PostMapping("/{id}/iniciar-diagnostico")
    public ResponseEntity<Void> iniciarDiagnostico(@PathVariable Long id) {
        service.iniciarDiagnostico(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/concluir-diagnostico")
    public ResponseEntity<Void> concluirDiagnostico(@PathVariable Long id, @RequestBody String laudo) {
        service.concluirDiagnostico(id, laudo);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/enviar-orcamento")
    public ResponseEntity<Void> enviarOrcamento(@PathVariable Long id) {
        service.enviarOrcamento(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reprovar")
    public ResponseEntity<Void> reprovar(@PathVariable Long id) {
        service.reprovarOrcamento(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/aprovar")
    public ResponseEntity<Void> aprovar(@PathVariable Long id) {
        service.aprovarOrcamento(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/verificar-estoque")
    public ResponseEntity<Void> verificarDisponibilidadePecas(@PathVariable Long id) {
        service.verificarDisponibilidadePecas(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/iniciar-execucao")
    public ResponseEntity<Void> iniciarExecucao(@PathVariable Long id) {
        service.iniciarExecucaoOS(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/finalizar-execucao")
    public ResponseEntity<Void> finalizarExecucao(@PathVariable Long id) {
        service.finalizarExecucaoOS(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/concluir")
    public ResponseEntity<Void> concluir(@PathVariable Long id) {
        service.entregarVeiculo(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServico> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarOuFalhar(id));
    }

}