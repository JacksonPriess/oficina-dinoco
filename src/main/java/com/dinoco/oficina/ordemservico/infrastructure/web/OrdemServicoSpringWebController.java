package com.dinoco.oficina.ordemservico.infrastructure.web;

import com.dinoco.oficina.autenticacao.infrastructure.security.ClientePrincipal;
import com.dinoco.oficina.ordemservico.adapters.controllers.OrdemServicoControllerClean;
import com.dinoco.oficina.ordemservico.application.usecases.commands.abrir.AbrirOrdemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.abrir.AbrirOrdemServicoOutput;
import com.dinoco.oficina.ordemservico.application.usecases.commands.aprovar.AprovarOrcamentoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.atualizarstatus.AtualizarStatusCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluir.ConcluirOrdemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirdiagnostico.ConcluirDiagnosticoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.decisaoclienteautenticado.DecisaoClienteAutenticadoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.decisaoclienteorcamento.DecisaoClienteCommand;
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
import com.dinoco.oficina.ordemservico.application.usecases.queries.listarfilatrabalho.ListarFilaTrabalhoOutput;
import com.dinoco.oficina.ordemservico.application.usecases.queries.listarfilatrabalho.ListarFilaTrabalhoQuery;
import com.dinoco.oficina.ordemservico.infrastructure.web.dto.*;
import com.dinoco.oficina.ordemservico.infrastructure.web.mapper.OrdemServicoWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PostMapping("/{osId}/iniciar-diagnostico")
    public ResponseEntity<Void> iniciarDiagnostico(@PathVariable Long osId) {
        IniciarDiagnosticoCommand input = mapper.toIniciarDiagnosticoCommand(osId);
        controllerClean.iniciarDiagnostico(input);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Concluir diagnóstico", description = "Informar o laudo técnico final e encaminhar para Orçamento")
    @PostMapping("/{osId}/concluir-diagnostico")
    public ResponseEntity<Void> concluirDiagnostico(@PathVariable Long osId, @Valid @RequestBody ConcluirDiagnosticoDto request) {
        ConcluirDiagnosticoCommand input = mapper.toConcluirDiagnosticoCommand(osId, request);
        controllerClean.concluirDiagnostico(input);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Enviar orçamento ao cliente")
    @PostMapping("/{osId}/enviar-orcamento")
    public ResponseEntity<LinkWhatsAppDto> enviarOrcamento(@PathVariable Long osId) {
        EnviarOrcamentoCommand input = mapper.toEnviarOrcamentoCommand(osId);
        EnviarOrcamentoOutput enviarOrcamentoOutput = controllerClean.enviarOrcamento(input);
        LinkWhatsAppDto response = mapper.toLinkWhatsAppResponse(enviarOrcamentoOutput);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reprovar orçamento", description = "Reprovar o orçamento enviado ao cliente")
    @PostMapping("/{osId}/reprovar")
    public ResponseEntity<Void> reprovarOrcamento(@PathVariable Long osId) {
        ReprovarOrcamentoCommand input = mapper.toReprovarOrcamentoCommand(osId);
        controllerClean.reprovarOrcamento(input);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Aprovar orçamento")
    @PostMapping("/{osId}/aprovar")
    public ResponseEntity<Void> aprovarOrcamento(@PathVariable Long osId) {
        AprovarOrcamentoCommand input = mapper.toAprovarOrcamentoCommand(osId);
        controllerClean.aprovarOrcamento(input);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Webhook: Receber decisão do cliente",
            description = "Recebe notificações externas de aprovação ou recusa do orçamento (Integração WhatsApp/Email)")
    @PostMapping("/webhooks/orcamentos/{codigoRastreio}")
    public ResponseEntity<Void> receberDecisaoClienteExterna(
            @PathVariable String codigoRastreio,
            @RequestHeader(value = "X-Dinoco-Secret", required = false) String secret,
            @RequestBody @Valid DecisaoClienteRequestDto request) {

        if (!"dinoco_webhook_secret_2026".equals(secret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        DecisaoClienteCommand input = mapper.toProcessarDecisaoClienteCommand(codigoRastreio, request);
        controllerClean.decisaoCliente(input);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Webhook: Atualização de status da OS",
            description = "Recebe requisições de sistemas externos (ex: link no e-mail) para avançar a máquina de estados da OS")
    @PostMapping("/webhooks/status/{codigoRastreio}")
    public ResponseEntity<Void> atualizarStatusExterna(
            @PathVariable String codigoRastreio,
            @RequestHeader(value = "X-Dinoco-Secret", required = false) String secret,
            @RequestBody @Valid AtualizarStatusDto request) {

        if (!"dinoco_webhook_secret_2026".equals(secret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AtualizarStatusCommand input = mapper.toAtualizarStatusCommand(codigoRastreio, request);
        controllerClean.atualizarStatus(input);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Verificar disponibilidade de peças no estoque")
    @PostMapping("/{osId}/verificar-estoque")
    public ResponseEntity<VerificarEstoqueResponseDto> verificarEstoque(@PathVariable Long osId) {
        VerificarEstoqueCommand command = mapper.toVerificarEstoqueCommand(osId);
        VerificarEstoqueOutput output = controllerClean.verificarEstoque(command);
        VerificarEstoqueResponseDto response = mapper.toVerificarEstoqueResponse(output);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Iniciar execução da OS.")
    @PostMapping("/{osId}/iniciar-execucao")
    public ResponseEntity<Void> iniciarExecucao(@PathVariable Long osId) {
        IniciarExecucaoCommand input = mapper.toIniciarExecucaoCommand(osId);
        controllerClean.iniciarExecucaoOS(input);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Finalizar execução da OS")
    @PostMapping("/{osId}/finalizar-execucao")
    public ResponseEntity<Void> finalizarExecucao(@PathVariable Long osId) {
        FinalizarExecucaoCommand input = mapper.toFinalizarExecucaoCommand(osId);
        controllerClean.finalizarExecucaoOS(input);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Concluir OS")
    @PostMapping("/{osId}/concluir")
    public ResponseEntity<Void> concluir(@PathVariable Long osId) {
        ConcluirOrdemServicoCommand input = mapper.toConcluirOrdemServicoCommand(osId);
        controllerClean.concluirOS(input);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar OS por id")
    @GetMapping("/{osId}")
    public ResponseEntity<BuscarOSPorIdResponseDto> buscarPorId(@PathVariable Long osId) {
        BuscarOSPorIdQuery query = mapper.toBuscarOSPorIdQuery(osId);
        BuscarOSPorIdOuput output = controllerClean.buscarOSPorId(query);
        BuscarOSPorIdResponseDto response = mapper.toBuscarOSPorIdResponse(output);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar resumo OS por código de rastreio")
    @GetMapping("/rastreio/{codigoRastreio}")
    public ResponseEntity<BuscarOSPorCodigoRastreioResponseDto> buscarPorCodigoRastreio(@PathVariable String codigoRastreio, @AuthenticationPrincipal ClientePrincipal cliente) {
        BuscarOSPorCodigoRastreioQuery query = mapper.toBuscarOSPorCodigoRastreioQuery(codigoRastreio, cliente.clienteId());
        BuscarOSPorCodigoRastreioOuput output = controllerClean.buscarOSPorCodigoRastreio(query);
        BuscarOSPorCodigoRastreioResponseDto response = mapper.toBuscarOSPorCodigoRastreioResponse(output);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cliente decide se vai aprovar ou recusar orçamento da OS")
    @PostMapping("/rastreio/{codigoRastreio}/decisao")
    public ResponseEntity<Void> decisaoClienteAutenticado(
            @PathVariable String codigoRastreio,
            @RequestBody @Valid DecisaoClienteRequestDto request,
            @AuthenticationPrincipal ClientePrincipal cliente) {
        DecisaoClienteAutenticadoCommand input = mapper.toProcessarDecisaoClienteAutenticado(codigoRastreio, cliente.clienteId(), request);
        controllerClean.decisaoClienteAutenticado(input);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listagem fila de trabalho")
    @GetMapping("/listagem")
    public ResponseEntity<FilaTrabalhosResponseDto> listarFilaDeTrabalho() {
        ListarFilaTrabalhoOutput output = controllerClean.listarFilaDeTrabalhos(new ListarFilaTrabalhoQuery());
        FilaTrabalhosResponseDto response = mapper.toListarAtivasRespose(output);
        return ResponseEntity.ok(response);
    }
}