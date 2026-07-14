package com.dinoco.oficina.cliente.infrastructure.web;

import com.dinoco.oficina.cliente.adapters.controllers.ClienteControllerClean;
import com.dinoco.oficina.cliente.application.usecases.commands.atualizar.AtualizarClienteCommand;
import com.dinoco.oficina.cliente.application.usecases.commands.atualizar.AtualizarClienteOutput;
import com.dinoco.oficina.cliente.application.usecases.commands.criar.CriarClienteCommand;
import com.dinoco.oficina.cliente.application.usecases.commands.criar.CriarClienteOutput;
import com.dinoco.oficina.cliente.application.usecases.commands.desativar.DesativarClienteCommand;
import com.dinoco.oficina.cliente.application.usecases.queries.buscarporid.BuscarClientePorIdOutput;
import com.dinoco.oficina.cliente.application.usecases.queries.buscarporid.BuscarClientePorIdQuery;
import com.dinoco.oficina.cliente.infrastructure.web.dto.ClienteRequestDto;
import com.dinoco.oficina.cliente.infrastructure.web.dto.ClienteResponseDto;
import com.dinoco.oficina.cliente.infrastructure.web.mapper.ClienteWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@Tag(name = "Clientes", description = "Cadastro de cliente")
@RestController
@RequestMapping("api/clientes")
public class ClienteSpringWebController {

    private final ClienteControllerClean controllerClean;
    private final ClienteWebMapper mapper;

    public ClienteSpringWebController(ClienteControllerClean controllerClean, ClienteWebMapper mapper) {
        this.controllerClean = controllerClean;
        this.mapper = mapper;
    }

    @Operation(summary = "Cadastrar cliente")
    @PostMapping
    public ResponseEntity<ClienteResponseDto> criar(@Valid @RequestBody ClienteRequestDto request) {
        CriarClienteCommand input = mapper.toInput(request);
        CriarClienteOutput output = controllerClean.criarCliente(input);
        ClienteResponseDto response = mapper.toResponse(output);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @Operation(summary = "Atualizar cliente")
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> atualizar(@PathVariable Long id, @RequestBody @Valid ClienteRequestDto request) {
        AtualizarClienteCommand command = mapper.toAtualizarCommand(id, request);
        AtualizarClienteOutput output = controllerClean.atualizarCliente(command);
        ClienteResponseDto response = mapper.toAtualizarResponse(output);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Desativar cliente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        DesativarClienteCommand command = mapper.toDesativarCommand(id);
        controllerClean.desativarCliente(command);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar cliente por código")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> buscarPorId(@PathVariable Long id) {
        BuscarClientePorIdQuery query = new BuscarClientePorIdQuery(id);
        BuscarClientePorIdOutput output = controllerClean.buscarPorId(query);
        ClienteResponseDto response = mapper.toQueryResponse(output);
        return ResponseEntity.ok(response);
    }

}
