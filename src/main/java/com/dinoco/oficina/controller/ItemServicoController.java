package com.dinoco.oficina.controller;

import com.dinoco.oficina.service.OrdemServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/itens-servico/{itemId}")
@RequiredArgsConstructor
public class ItemServicoController {

    private final OrdemServicoService osService;

    @PatchMapping("/iniciar")
    public ResponseEntity<Void> iniciarItem(@PathVariable Long itemId) {
        osService.iniciarItemServico(itemId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/concluir")
    public ResponseEntity<Void> concluirItem(@PathVariable Long itemId) {
        osService.concluirItemServico(itemId);
        return ResponseEntity.ok().build();
    }
}