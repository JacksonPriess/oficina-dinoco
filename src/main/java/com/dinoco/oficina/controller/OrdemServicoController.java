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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

@Tag(name = "7. Ordem de servico", description = "Criação e acompanhamento da Ordem de Serviço")
@RestController
@RequestMapping("api/ordens-servico")
@RequiredArgsConstructor
public class OrdemServicoController {

    private final OrdemServicoService service;

    @Operation(summary = "01. Abrir ordem de serviço", description = "Informar dados iniciais da OS")
    @PostMapping
    public ResponseEntity<OrdemServicoResponseDto> abrirOS(@RequestBody @Valid OrdemServicoRequestDto dto) {
        OrdemServicoResponseDto response = service.abrirOs(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @Operation(summary = "02. Iniciar diagnóstico")
    @PostMapping("/{id}/iniciar-diagnostico")
    public ResponseEntity<OrdemServicoResponseDto> iniciarDiagnostico(@PathVariable Long id) {
        OrdemServicoResponseDto response = service.iniciarDiagnostico(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "03. Concluir diagnóstico", description = "Informar o laudo técnico final e encaminhar para Orçamento")
    @PostMapping("/{id}/concluir-diagnostico")
    public ResponseEntity<OrdemServicoResponseDto> concluirDiagnostico(@PathVariable Long id, @Valid @RequestBody ConcluirDiagnosticoDto dto) {
        OrdemServicoResponseDto response = service.concluirDiagnostico(id, dto.laudo());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "04. Enviar orçamento")
    @PostMapping("/{id}/enviar-orcamento")
    public ResponseEntity<LinkWhatsAppDto> enviarOrcamento(@PathVariable Long id) {
        return ResponseEntity.ok(service.enviarOrcamento(id));
    }

    @Operation(summary = "05. Reprovar orçamento")
    @PostMapping("/{id}/reprovar")
    public ResponseEntity<OrdemServicoResponseDto> reprovar(@PathVariable Long id) {
        OrdemServicoResponseDto response = service.reprovarOrcamento(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "06. Aprovar orçamento")
    @PostMapping("/{id}/aprovar")
    public ResponseEntity<OrdemServicoResponseDto> aprovar(@PathVariable Long id) {
        OrdemServicoResponseDto response = service.aprovarOrcamento(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "07. Verificar estoque", description = "Ação necessária quando a OS fica Aguadando Fornecedor")
    @PostMapping("/{id}/verificar-estoque")
    public ResponseEntity<OrdemServicoResponseDto> verificarDisponibilidadePecas(@PathVariable Long id) {
        OrdemServicoResponseDto response = service.verificarDisponibilidadePecas(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "08. Iniciar execução do serviço")
    @PostMapping("/{id}/iniciar-execucao")
    public ResponseEntity<OrdemServicoResponseDto> iniciarExecucao(@PathVariable Long id) {
        OrdemServicoResponseDto response = service.iniciarExecucaoOS(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "09. Finalizar execução do serviço")
    @PostMapping("/{id}/finalizar-execucao")
    public ResponseEntity<OrdemServicoResponseDto> finalizarExecucao(@PathVariable Long id) {
        OrdemServicoResponseDto response = service.finalizarExecucaoOS(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "10. Concluir OS")
    @PostMapping("/{id}/concluir")
    public ResponseEntity<OrdemServicoResponseDto> concluir(@PathVariable Long id) {
        OrdemServicoResponseDto response = service.entregarVeiculo(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "11. Buscar resumo OS por código")
    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "12. Buscar resumo OS por número de rastreio")
    @GetMapping("/rastreio/{codigoRastreio}")
    public ResponseEntity<OrdemServicoPublicResponseDto> buscarPorCodigoRastreio(@PathVariable String codigoRastreio) {
        return ResponseEntity.ok(service.buscarPorCodigoRastreio(codigoRastreio));
    }

    @Operation(summary = "13. Buscar detalhamento da OS por número de rastreio")
    @GetMapping("/detalhes/{codigoRastreio}")
    public ResponseEntity<OrdemServicoDetalhadaResponseDto> getDetalhes(@PathVariable String codigoRastreio) {
        OrdemServicoDetalhadaResponseDto response = service.buscarDetalhesPorCodigoRastreio(codigoRastreio);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "14. Listar todas as Ordens de Serviço (com paginação)")
    @GetMapping
    public ResponseEntity<Page<OrdemServicoResponseDto>> listarTodas(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<OrdemServicoResponseDto> response = service.listarTodas(pageable);
        return ResponseEntity.ok(response);
    }
}