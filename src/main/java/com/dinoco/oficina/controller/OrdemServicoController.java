package com.dinoco.oficina.controller;

import com.dinoco.oficina.dto.*;
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

    @Operation(summary = "Buscar OS por código")
    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Buscar OS por número de rastreio")
    @GetMapping("/rastreio/{codigoRastreio}")
    public ResponseEntity<OrdemServicoResponseDto> buscarPorCodigoRastreio(@PathVariable String codigoRastreio) {
        //TODO - Simplificar retorno da OS quando for por código de rastreio.
        return ResponseEntity.ok(service.buscarPorCodigoRastreio(codigoRastreio));
    }

    @Operation(summary = "Iniciar diagnóstico")
    @PostMapping("/{id}/iniciar-diagnostico")
    public ResponseEntity<Void> iniciarDiagnostico(@PathVariable Long id) {
        service.iniciarDiagnostico(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Concluir diagnóstico, informando o laudo técnico final e encaminhar para Orçamento")
    @PostMapping("/{id}/concluir-diagnostico")
    public ResponseEntity<Void> concluirDiagnostico(@PathVariable Long id, @Valid @RequestBody ConcluirDiagnosticoDto dto) {
        service.concluirDiagnostico(id, dto.laudo());
        return ResponseEntity.noContent().build();
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
}