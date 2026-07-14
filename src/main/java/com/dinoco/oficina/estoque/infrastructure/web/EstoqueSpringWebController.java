package com.dinoco.oficina.estoque.infrastructure.web;

import com.dinoco.oficina.estoque.adapters.controllers.EstoqueControllerClean;
import com.dinoco.oficina.estoque.application.usecases.commands.ajustarinventario.AjustarInventarioCommand;
import com.dinoco.oficina.estoque.application.usecases.commands.registrarentrada.RegistrarEntradaCommand;
import com.dinoco.oficina.estoque.infrastructure.web.dto.EntradaSaldoEstoqueRequestDto;
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
    @PostMapping("/{produtoId}/estoque/ajuste")
    public ResponseEntity<Void> ajustarSaldoEstoque(@PathVariable Long produtoId, @RequestBody @Valid SaldoEstoqueRequestDto request) {
        AjustarInventarioCommand input = mapper.toInput(produtoId, request);
        controllerClean.ajustarInventario(input);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Entrada de compra")
    @PostMapping("/{produtoId}/estoque/entrada-compra")
    public ResponseEntity<Void> registrarEntradaSaldo(@PathVariable Long produtoId, @RequestBody @Valid EntradaSaldoEstoqueRequestDto request) {
        RegistrarEntradaCommand input = mapper.toInputEntradaCompra(produtoId, request);
        controllerClean.registrarEntrada(input);
        return ResponseEntity.ok().build();
    }

}