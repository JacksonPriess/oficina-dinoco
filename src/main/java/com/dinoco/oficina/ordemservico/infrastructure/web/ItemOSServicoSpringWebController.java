package com.dinoco.oficina.ordemservico.infrastructure.web;

import com.dinoco.oficina.ordemservico.adapters.controllers.ItemOSServicoControllerClean;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemservico.AdicionarItemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemservico.AlterarItemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirexecucaoitemservico.ConcluirExecucaoItemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucaoitemservico.IniciarExecucaoItemServicoCommand;
import com.dinoco.oficina.ordemservico.infrastructure.web.dto.ConcluirExecucaoItemServicoDto;
import com.dinoco.oficina.ordemservico.infrastructure.web.dto.ItemOSServicoAdicionarDto;
import com.dinoco.oficina.ordemservico.infrastructure.web.mapper.OrdemServicoWebMapper;
import com.dinoco.oficina.ordemservico.infrastructure.web.dto.ItemOSServicoAlterarDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Itens da OS - Serviços", description = "Cadastro de itens de serviços da OS")
@RestController
@RequestMapping("api/ordens-servico/{osId}/servicos")
public class ItemOSServicoSpringWebController {

    private final ItemOSServicoControllerClean controllerClean;
    private final OrdemServicoWebMapper mapper;

    public ItemOSServicoSpringWebController(ItemOSServicoControllerClean controllerClean, OrdemServicoWebMapper mapper) {
        this.controllerClean = controllerClean;
        this.mapper = mapper;
    }

    @Operation(summary = "Adicionar item de serviço")
    @PostMapping
    public ResponseEntity<Void> adicionarServico(@PathVariable Long osId, @Valid @RequestBody ItemOSServicoAdicionarDto request) {
        AdicionarItemServicoCommand input = mapper.toAdicionarItemServicoCommand(osId, request);
        controllerClean.adicionarItemServico(input);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Alterar item de serviço")
    @PutMapping("/{itemId}")
    public ResponseEntity<Void> alterarServico(@PathVariable Long osId, @PathVariable Long itemId, @Valid @RequestBody ItemOSServicoAlterarDto dto) {
        AlterarItemServicoCommand input = mapper.toAlterarItemServicoCommand(osId, itemId, dto);
        controllerClean.alterarItemServico(input);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Iniciar execução")
    @PostMapping("/{itemId}/iniciar-execucao")
    public ResponseEntity<Void> iniciarExecucaoServico(@PathVariable Long osId, @PathVariable Long itemId) {
        IniciarExecucaoItemServicoCommand input = mapper.toIniciarExecucaoItemServicoCommand(osId, itemId);
        controllerClean.iniciarExecucaoItemServico(input);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Concluir execução")
    @PostMapping("/{itemId}/concluir-execucao")
    public ResponseEntity<Void> concluirExecucaoServico(@PathVariable Long osId, @PathVariable Long itemId, @Valid @RequestBody ConcluirExecucaoItemServicoDto request) {
        ConcluirExecucaoItemServicoCommand input = mapper.toConcluirExecucaoItemServicoCommand(osId, itemId, request);
        controllerClean.concluirExecucaoItemServico(input);
        return ResponseEntity.noContent().build();
    }

}