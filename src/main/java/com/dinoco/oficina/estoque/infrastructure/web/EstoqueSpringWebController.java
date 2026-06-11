package com.dinoco.oficina.estoque.infrastructure.web;

import com.dinoco.oficina.estoque.adapters.controllers.EstoqueControllerClean;
import com.dinoco.oficina.estoque.application.usecases.commands.ajustarinventario.AjustarInventarioCommand;
import com.dinoco.oficina.estoque.infrastructure.web.dto.SaldoEstoqueRequestDto;
import com.dinoco.oficina.estoque.infrastructure.web.mapper.EstoqueWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Estoque", description = "Controle de estoque")
@RestController
@RequestMapping("api/produtos")
public class EstoqueSpringWebController {

    private final EstoqueControllerClean controllerClean;
    private final EstoqueWebMapper mapper;

    public EstoqueSpringWebController(EstoqueControllerClean controllerClean, EstoqueWebMapper mapper) {
        this.controllerClean = controllerClean;
        this.mapper = mapper;
    }

    @Operation(summary = "Ajuste no estoque")
    @PostMapping("/{id}/estoque/ajuste")
    public ResponseEntity<Void> ajustarSaldoEstoque(@RequestBody @Valid SaldoEstoqueRequestDto request) {
        AjustarInventarioCommand input = mapper.toInput(request);
        controllerClean.ajustarInventario(input);
        return ResponseEntity.ok().build();
    }

}