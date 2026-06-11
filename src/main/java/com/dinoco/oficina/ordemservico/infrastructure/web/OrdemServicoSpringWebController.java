package com.dinoco.oficina.ordemservico.infrastructure.web;

import com.dinoco.oficina.ordemservico.adapters.controllers.OrdemServicoControllerClean;
import com.dinoco.oficina.ordemservico.application.usecases.commands.abrir.AbrirOrdemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.abrir.AbrirOrdemServicoOutput;
import com.dinoco.oficina.ordemservico.application.usecases.commands.aprovar.AprovarOrcamentoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirdiagnostico.ConcluirDiagnosticoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.enviarorcamento.EnviarOrcamentoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.enviarorcamento.EnviarOrcamentoOutput;
import com.dinoco.oficina.ordemservico.application.usecases.commands.finalizarexecucao.FinalizarExecucaoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciardiagnostico.IniciarDiagnosticoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucao.IniciarExecucaoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.reprovar.ReprovarOrcamentoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.verificarestoque.VerificarEstoqueCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.verificarestoque.VerificarEstoqueOutput;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarporid.BuscarOSPorIdOuput;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarporid.BuscarOSPorIdQuery;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarpornumero.BuscarOSPorCodigoRastreioOuput;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarpornumero.BuscarOSPorCodigoRastreioQuery;
import com.dinoco.oficina.ordemservico.infrastructure.web.dto.*;
import com.dinoco.oficina.ordemservico.infrastructure.web.mapper.OrdemServicoWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@Tag(name = "Ordem de servico", description = "Criação e acompanhamento da Ordem de Serviço")
@RestController
@RequestMapping("api/ordens-servico")
public class OrdemServicoSpringWebController {

    private final OrdemServicoControllerClean controllerClean;
    private final OrdemServicoWebMapper mapper;

    public OrdemServicoSpringWebController(OrdemServicoControllerClean controllerClean, OrdemServicoWebMapper mapper) {
        this.controllerClean = controllerClean;
        this.mapper = mapper;
    }

    @Operation(summary = "Abrir ordem de serviço", description = "Informar dados iniciais da OS")
    @PostMapping
    public ResponseEntity<OrdemServicoResponseDto> abrirOS(@RequestBody @Valid OrdemServicoRequestDto request) {
        AbrirOrdemServicoCommand input = mapper.toInput(request);
        AbrirOrdemServicoOutput output = controllerClean.abrirOrdemServico(input);
        OrdemServicoResponseDto response = mapper.toResponse(output);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @Operation(summary = "Iniciar diagnóstico")
    @PostMapping("/{id}/iniciar-diagnostico")
    public ResponseEntity<Void> iniciarDiagnostico(@PathVariable Long id) {
        IniciarDiagnosticoCommand input = mapper.toIniciarDiagnosticoCommand(id);
        controllerClean.iniciarDiagnostico(input);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Concluir diagnóstico", description = "Informar o laudo técnico final e encaminhar para Orçamento")
    @PostMapping("/{id}/concluir-diagnostico")
    public ResponseEntity<Void> concluirDiagnostico(@PathVariable Long id, @Valid @RequestBody ConcluirDiagnosticoDto request) {
        ConcluirDiagnosticoCommand input = mapper.toConcluirDiagnosticoCommand(id, request);
        controllerClean.concluirDiagnostico(input);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Enviar orçamento ao cliente")
    @PostMapping("/{id}/enviar-orcamento")
    public ResponseEntity<LinkWhatsAppDto> enviarOrcamento(@PathVariable Long id) {
        EnviarOrcamentoCommand input = mapper.toEnviarOrcamentoCommand(id);
        EnviarOrcamentoOutput enviarOrcamentoOutput = controllerClean.enviarOrcamento(input);
        LinkWhatsAppDto response = mapper.toLinkWhatsAppResponse(enviarOrcamentoOutput);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reprovar orçamento", description = "Reprovar o orçamento enviado ao cliente")
    @PostMapping("/{id}/reprovar")
    public ResponseEntity<Void> reprovarOrcamento(@PathVariable Long id) {
        ReprovarOrcamentoCommand input = mapper.toReprovarOrcamentoCommand(id);
        controllerClean.reprovarOrcamento(input);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Aprovar orçamento")
    @PostMapping("/{id}/aprovar")
    public ResponseEntity<Void> aprovarOrcamento(@PathVariable Long id) {
        AprovarOrcamentoCommand input = mapper.toAprovarOrcamentoCommand(id);
        controllerClean.aprovarOrcamento(input);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Verificar disponibilidade de peças no estoque")
    @PostMapping("/{id}/verificar-estoque")
    public ResponseEntity<VerificarEstoqueResponseDto> verificarEstoque(@PathVariable Long id) {
        VerificarEstoqueCommand command = mapper.toVerificarEstoqueCommand(id);
        VerificarEstoqueOutput output = controllerClean.verificarEstoque(command);
        VerificarEstoqueResponseDto response = mapper.toVerificarEstoqueResponse(output);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Iniciar execução dos serviços da OS")
    @PostMapping("/{id}/iniciar-execucao")
    public ResponseEntity<Void> iniciarExecucao(@PathVariable Long id) {
        IniciarExecucaoCommand input = mapper.toIniciarExecucaoCommand(id);
        controllerClean.iniciarExecucaoOS(input);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Finalizar execução dos serviços")
    @PostMapping("/{id}/finalizar-execucao")
    public ResponseEntity<Void> finalizarExecucao(@PathVariable Long id) {
        FinalizarExecucaoCommand input = mapper.toFinalizarExecucaoCommand(id);
        controllerClean.finalizarExecucaoOS(input);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Concluir OS")
    @PostMapping("/{id}/concluir")
    public ResponseEntity<Void> concluir(@PathVariable Long id) {
        FinalizarExecucaoCommand input = mapper.toFinalizarExecucaoCommand(id);
        controllerClean.finalizarExecucaoOS(input);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar OS por id")
    @GetMapping("/{id}")
    public ResponseEntity<BuscarOSPorIdResponseDto> buscarPorId(@PathVariable Long id) {
        BuscarOSPorIdQuery query = mapper.toBuscarOSPorIdQuery(id);
        BuscarOSPorIdOuput output = controllerClean.buscarOSPorId(query);
        BuscarOSPorIdResponseDto response = mapper.toBuscarOSPorIdResponse(output);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar resumo OS por código de rastreio")
    @GetMapping("/rastreio/{codigoRastreio}")
    public ResponseEntity<BuscarOSPorCodigoRastreioResponseDto> buscarPorCodigoRastreio(@PathVariable String codigoRastreio) {
        BuscarOSPorCodigoRastreioQuery query = mapper.toBuscarOSPorCodigoRastreioQuery(codigoRastreio);
        BuscarOSPorCodigoRastreioOuput output = controllerClean.buscarOSPorCodigoRastreio(query);
        BuscarOSPorCodigoRastreioResponseDto response = mapper.toBuscarOSPorCodigoRastreioResponse(output);
        return ResponseEntity.ok(response);
    }

}