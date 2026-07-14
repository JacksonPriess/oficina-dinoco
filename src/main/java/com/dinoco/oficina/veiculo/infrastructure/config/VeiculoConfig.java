package com.dinoco.oficina.veiculo.infrastructure.config;


import com.dinoco.oficina.veiculo.adapters.controllers.VeiculoControllerClean;
import com.dinoco.oficina.veiculo.application.gateways.VeiculoCommandGateway;
import com.dinoco.oficina.veiculo.application.gateways.VeiculoQueryGateway;
import com.dinoco.oficina.veiculo.application.usecases.commands.atualizar.AtualizarVeiculoHandler;
import com.dinoco.oficina.veiculo.application.usecases.commands.atualizar.AtualizarVeiculoUseCase;
import com.dinoco.oficina.veiculo.application.usecases.commands.criar.CriarVeiculoHandler;
import com.dinoco.oficina.veiculo.application.usecases.commands.criar.CriarVeiculoUseCase;
import com.dinoco.oficina.veiculo.application.usecases.commands.desativar.DesativarVeiculoHandler;
import com.dinoco.oficina.veiculo.application.usecases.commands.desativar.DesativarVeiculoUseCase;
import com.dinoco.oficina.veiculo.application.usecases.queries.buscarporid.BuscarVeiculoPorIdHandler;
import com.dinoco.oficina.veiculo.application.usecases.queries.buscarporid.BuscarVeiculoPorIdUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VeiculoConfig {

    @Bean
    public CriarVeiculoUseCase criarVeiculoUseCase(VeiculoCommandGateway veiculoCommandGateway, VeiculoQueryGateway veiculoQueryGateway) {
        return new CriarVeiculoHandler(veiculoCommandGateway, veiculoQueryGateway);
    }

    @Bean
    public AtualizarVeiculoUseCase atualizarVeiculoUseCase(VeiculoCommandGateway commandGateway) {
        return new AtualizarVeiculoHandler(commandGateway);
    }

    @Bean
    public DesativarVeiculoUseCase desativarVeiculoUseCase(VeiculoCommandGateway commandGateway) {
        return new DesativarVeiculoHandler(commandGateway);
    }

    @Bean
    public BuscarVeiculoPorIdUseCase buscarVeiculoPorIdUseCase(VeiculoQueryGateway queryGateway) {
        return new BuscarVeiculoPorIdHandler(queryGateway);
    }

    @Bean
    public VeiculoControllerClean veiculoControllerClean(
            CriarVeiculoUseCase criarVeiculoUseCase,
            AtualizarVeiculoUseCase atualizarVeiculoUseCase,
            DesativarVeiculoUseCase desativarVeiculoUseCase,
            BuscarVeiculoPorIdUseCase buscarVeiculoPorIdUseCase) {
        return new VeiculoControllerClean(criarVeiculoUseCase, atualizarVeiculoUseCase, desativarVeiculoUseCase, buscarVeiculoPorIdUseCase);
    }
}