package com.dinoco.oficina.funcionario.infrastructure.config;

import com.dinoco.oficina.funcionario.adapters.controllers.FuncionarioControllerClean;
import com.dinoco.oficina.funcionario.application.gateways.FuncionarioCommandGateway;
import com.dinoco.oficina.funcionario.application.gateways.FuncionarioQueryGateway;
import com.dinoco.oficina.funcionario.application.gateways.UsuarioSistemaGateway;
import com.dinoco.oficina.funcionario.application.usecases.commands.atualizar.AtualizarFuncionarioHandler;
import com.dinoco.oficina.funcionario.application.usecases.commands.atualizar.AtualizarFuncionarioUseCase;
import com.dinoco.oficina.funcionario.application.usecases.commands.criar.CriarFuncionarioHandler;
import com.dinoco.oficina.funcionario.application.usecases.commands.criar.CriarFuncionarioUseCase;
import com.dinoco.oficina.funcionario.application.usecases.commands.desativar.DesativarFuncionarioHandler;
import com.dinoco.oficina.funcionario.application.usecases.commands.desativar.DesativarFuncionarioUseCase;
import com.dinoco.oficina.funcionario.application.usecases.commands.resetarsenha.ResetarSenhaFuncionarioHandler;
import com.dinoco.oficina.funcionario.application.usecases.commands.resetarsenha.ResetarSenhaFuncionarioUseCase;
import com.dinoco.oficina.funcionario.application.usecases.queries.buscarporid.BuscarFuncionarioPorIdHandler;
import com.dinoco.oficina.funcionario.application.usecases.queries.buscarporid.BuscarFuncionarioPorIdUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FuncionarioConfig {

    @Bean
    public CriarFuncionarioUseCase criarFuncionarioUseCase(FuncionarioCommandGateway funcionarioCommandGateway, FuncionarioQueryGateway funcionarioQueryGateway, UsuarioSistemaGateway usuarioGateway) {
        return new CriarFuncionarioHandler(funcionarioCommandGateway, funcionarioQueryGateway, usuarioGateway);
    }

    @Bean
    public AtualizarFuncionarioUseCase atualizarFuncionarioUseCase(FuncionarioCommandGateway commandGateway) {
        return new AtualizarFuncionarioHandler(commandGateway);
    }

    @Bean
    public DesativarFuncionarioUseCase desativarFuncionarioUseCase(FuncionarioCommandGateway commandGateway) {
        return new DesativarFuncionarioHandler(commandGateway);
    }

    @Bean
    public BuscarFuncionarioPorIdUseCase buscarFuncionarioPorIdUseCase(FuncionarioQueryGateway queryGateway) {
        return new BuscarFuncionarioPorIdHandler(queryGateway);
    }

    @Bean
    public ResetarSenhaFuncionarioUseCase resetarSenhaFuncionarioUseCase(FuncionarioCommandGateway commandGateway, UsuarioSistemaGateway usuarioSistemaGateway) {
        return new ResetarSenhaFuncionarioHandler(commandGateway, usuarioSistemaGateway);
    }

    @Bean
    public FuncionarioControllerClean funcionarioControllerClean(
            CriarFuncionarioUseCase criarFuncionarioUseCase,
            AtualizarFuncionarioUseCase atualizarFuncionarioUseCase,
            DesativarFuncionarioUseCase desativarFuncionarioUseCase,
            BuscarFuncionarioPorIdUseCase buscarFuncionarioPorIdUseCase,
            ResetarSenhaFuncionarioUseCase resetarSenhaFuncionarioUseCase) {
        return new FuncionarioControllerClean(
                criarFuncionarioUseCase,
                atualizarFuncionarioUseCase,
                desativarFuncionarioUseCase,
                buscarFuncionarioPorIdUseCase,
                resetarSenhaFuncionarioUseCase);
    }
}
