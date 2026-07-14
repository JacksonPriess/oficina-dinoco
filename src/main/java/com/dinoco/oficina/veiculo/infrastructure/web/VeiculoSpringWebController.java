package com.dinoco.oficina.veiculo.infrastructure.web;

import com.dinoco.oficina.veiculo.adapters.controllers.VeiculoControllerClean;
import com.dinoco.oficina.veiculo.application.usecases.commands.atualizar.AtualizarVeiculoCommand;
import com.dinoco.oficina.veiculo.application.usecases.commands.atualizar.AtualizarVeiculoOutput;
import com.dinoco.oficina.veiculo.application.usecases.commands.criar.CriarVeiculoCommand;
import com.dinoco.oficina.veiculo.application.usecases.commands.criar.CriarVeiculoOutput;
import com.dinoco.oficina.veiculo.application.usecases.commands.desativar.DesativarVeiculoCommand;
import com.dinoco.oficina.veiculo.application.usecases.queries.buscarporid.BuscarVeiculoPorIdOutput;
import com.dinoco.oficina.veiculo.application.usecases.queries.buscarporid.BuscarVeiculoPorIdQuery;
import com.dinoco.oficina.veiculo.infrastructure.web.dto.VeiculoRequestDto;
import com.dinoco.oficina.veiculo.infrastructure.web.dto.VeiculoResponseDto;
import com.dinoco.oficina.veiculo.infrastructure.web.mapper.VeiculoWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@Tag(name = "Veículos", description = "Cadastro de veículo")
@RestController
@RequestMapping("api/veiculos")
public class VeiculoSpringWebController {

    private final VeiculoControllerClean controllerClean;
    private final VeiculoWebMapper mapper;

    public VeiculoSpringWebController(VeiculoControllerClean controllerClean, VeiculoWebMapper mapper) {
        this.controllerClean = controllerClean;
        this.mapper = mapper;
    }

    @Operation(summary = "Cadastrar veículo")
    @PostMapping
    public ResponseEntity<VeiculoResponseDto> criar(@Valid @RequestBody VeiculoRequestDto request) {
        CriarVeiculoCommand input = mapper.toInput(request);
        CriarVeiculoOutput output = controllerClean.criarVeiculo(input);
        VeiculoResponseDto response = mapper.toResponse(output);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @Operation(summary = "Atualizar veículo")
    @PutMapping("/{id}")
    public ResponseEntity<VeiculoResponseDto> atualizar(@PathVariable Long id, @RequestBody @Valid VeiculoRequestDto request) {
        AtualizarVeiculoCommand command = mapper.toAtualizarCommand(id, request);
        AtualizarVeiculoOutput output = controllerClean.atualizarVeiculo(command);
        VeiculoResponseDto response = mapper.toAtualizarResponse(output);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Desativar veículo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        DesativarVeiculoCommand command = mapper.toDesativarCommand(id);
        controllerClean.desativarVeiculo(command);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar veículo por código")
    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponseDto> buscarPorId(@PathVariable Long id) {
        BuscarVeiculoPorIdQuery query = new BuscarVeiculoPorIdQuery(id);
        BuscarVeiculoPorIdOutput output = controllerClean.buscarPorId(query);
        VeiculoResponseDto response = mapper.toQueryResponse(output);
        return ResponseEntity.ok(response);
    }

}
