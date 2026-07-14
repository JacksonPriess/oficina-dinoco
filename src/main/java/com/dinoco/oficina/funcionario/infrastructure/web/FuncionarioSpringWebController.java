package com.dinoco.oficina.funcionario.infrastructure.web;


import com.dinoco.oficina.cliente.application.usecases.queries.buscarporid.BuscarClientePorIdQuery;
import com.dinoco.oficina.funcionario.adapters.controllers.FuncionarioControllerClean;
import com.dinoco.oficina.funcionario.application.usecases.commands.atualizar.AtualizarFuncionarioCommand;
import com.dinoco.oficina.funcionario.application.usecases.commands.atualizar.AtualizarFuncionarioOutput;
import com.dinoco.oficina.funcionario.application.usecases.commands.criar.CriarFuncionarioCommand;
import com.dinoco.oficina.funcionario.application.usecases.commands.criar.CriarFuncionarioOutput;
import com.dinoco.oficina.funcionario.application.usecases.commands.desativar.DesativarFuncionarioCommand;
import com.dinoco.oficina.funcionario.application.usecases.commands.resetarsenha.ResetarSenhaFuncionarioCommand;
import com.dinoco.oficina.funcionario.application.usecases.commands.resetarsenha.ResetarSenhaFuncionarioOutput;
import com.dinoco.oficina.funcionario.application.usecases.queries.buscarporid.BuscarFuncionarioPorIdOutput;
import com.dinoco.oficina.funcionario.application.usecases.queries.buscarporid.BuscarFuncionarioPorIdQuery;
import com.dinoco.oficina.funcionario.infrastructure.web.dto.FuncionarioRequestDto;
import com.dinoco.oficina.funcionario.infrastructure.web.dto.FuncionarioResponseDto;
import com.dinoco.oficina.funcionario.infrastructure.web.dto.SenhaResetadaResponseDto;
import com.dinoco.oficina.funcionario.infrastructure.web.mapper.FuncionarioWebMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@Tag(name = "3. Funcionários", description = "Cadastro de funcionário")
@RestController
@RequestMapping("api/funcionarios")

public class FuncionarioSpringWebController {

    private final FuncionarioControllerClean controllerClean;
    private final FuncionarioWebMapper mapper;

    public FuncionarioSpringWebController(FuncionarioControllerClean controllerClean, FuncionarioWebMapper mapper) {
        this.controllerClean = controllerClean;
        this.mapper = mapper;
    }

    @Operation(summary = "Cadastrar funcionário")
    @PostMapping
    public ResponseEntity<FuncionarioResponseDto> criar(@Valid @RequestBody FuncionarioRequestDto request) {
        CriarFuncionarioCommand input = mapper.toInputCriar(request);
        CriarFuncionarioOutput output = controllerClean.criarFuncionario(input);
        FuncionarioResponseDto response = mapper.toResponseCriar(output);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @Operation(summary = "Atualizar funcionário")
    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDto> atualizar(@PathVariable Long id, @RequestBody @Valid FuncionarioRequestDto request) {
        AtualizarFuncionarioCommand input = mapper.toInputAtualizar(id, request);
        AtualizarFuncionarioOutput output = controllerClean.atualizarFuncionario(input);
        FuncionarioResponseDto response = mapper.toResponseAtualizar(output);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Resetar senha funcionário - Gerar um novo acesso temporário.")
    @PutMapping("/{id}/reset-senha")
    public ResponseEntity<SenhaResetadaResponseDto> resetarSenha(@PathVariable Long id) {
        ResetarSenhaFuncionarioCommand input = mapper.toResetarSenha(id);
        ResetarSenhaFuncionarioOutput output = controllerClean.resetarSenha(input);
        SenhaResetadaResponseDto response = mapper.toResponseSenhaResetada(output);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Desativar funcionário")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        DesativarFuncionarioCommand command = mapper.toDesativarCommand(id);
        controllerClean.desativarFuncionario(command);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar funcionário por código")
    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDto> buscarPorId(@PathVariable Long id) {
        BuscarFuncionarioPorIdQuery query = new BuscarFuncionarioPorIdQuery(id);
        BuscarFuncionarioPorIdOutput output = controllerClean.buscarPorId(query);
        FuncionarioResponseDto response = mapper.toQueryResponse(output);
        return ResponseEntity.ok(response);
    }

}
