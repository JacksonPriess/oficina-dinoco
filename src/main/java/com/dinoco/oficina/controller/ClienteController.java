package com.dinoco.oficina.controller;

import com.dinoco.oficina.dto.ClienteRequestDto;
import com.dinoco.oficina.dto.ClienteResponseDto;
import com.dinoco.oficina.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@Tag(name = "Clientes", description = "Cadastro de cliente")
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastrar cliente")
    @PostMapping
    public ResponseEntity<ClienteResponseDto> criar(@Valid @RequestBody ClienteRequestDto request) {
        ClienteResponseDto response = service.criar(request);

        // Retorna 201 Created com a URL do novo recurso no Header Location
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @Operation(summary = "Buscar cliente por código")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Atualizar cliente")
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> atualizar(@PathVariable Long id, @RequestBody @Valid ClienteRequestDto request) {
        ClienteResponseDto response = service.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Desativar cliente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
