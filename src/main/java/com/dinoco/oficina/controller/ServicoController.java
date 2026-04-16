package com.dinoco.oficina.controller;

import com.dinoco.oficina.dto.ServicoRequestDto;
import com.dinoco.oficina.dto.ServicoResponseDto;
import com.dinoco.oficina.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {

    private final ServicoService service;

    public ServicoController(ServicoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ServicoResponseDto> criar(@RequestBody @Valid ServicoRequestDto request) {
        ServicoResponseDto response = service.criar(request);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponseDto> atualizar(@PathVariable Long id, @RequestBody @Valid ServicoRequestDto request) {
        ServicoResponseDto response = service.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.noContent().build();
    }
}