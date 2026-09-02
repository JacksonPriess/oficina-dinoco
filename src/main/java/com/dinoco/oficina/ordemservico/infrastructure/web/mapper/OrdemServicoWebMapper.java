package com.dinoco.oficina.ordemservico.infrastructure.web.mapper;

import com.dinoco.oficina.ordemservico.application.usecases.commands.abrir.AbrirOrdemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.abrir.AbrirOrdemServicoItemProdutoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.abrir.AbrirOrdemServicoItemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.abrir.AbrirOrdemServicoOutput;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemproduto.AdicionarItemProdutoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemservico.AdicionarItemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemproduto.AlterarItemProdutoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemservico.AlterarItemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.aprovar.AprovarOrcamentoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.atualizarstatus.AtualizarStatusCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluir.ConcluirOrdemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirdiagnostico.ConcluirDiagnosticoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirexecucaoitemservico.ConcluirExecucaoItemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.decisaoclienteorcamento.DecisaoClienteCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.enviarorcamento.EnviarOrcamentoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.enviarorcamento.EnviarOrcamentoOutput;
import com.dinoco.oficina.ordemservico.application.usecases.commands.finalizarexecucao.FinalizarExecucaoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciardiagnostico.IniciarDiagnosticoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucao.IniciarExecucaoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.iniciarexecucaoitemservico.IniciarExecucaoItemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.reprovar.ReprovarOrcamentoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.verificarestoque.VerificarEstoqueCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.verificarestoque.VerificarEstoqueOutput;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarporid.BuscarOSPorIdOuput;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarporid.BuscarOSPorIdQuery;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarpornumero.BuscarOSPorCodigoRastreioOuput;
import com.dinoco.oficina.ordemservico.application.usecases.queries.buscarpornumero.BuscarOSPorCodigoRastreioQuery;
import com.dinoco.oficina.ordemservico.application.usecases.queries.listarfilatrabalho.ListarFilaTrabalhoOutput;
import com.dinoco.oficina.ordemservico.infrastructure.web.dto.*;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrdemServicoWebMapper {

    @Mapping(target = "produtos", source = "request.produtos")
    @Mapping(target = "servicos", source = "request.servicos")
    AbrirOrdemServicoCommand toInput(@Valid OrdemServicoRequestDto request);

    AbrirOrdemServicoItemProdutoCommand toAbrirOrdemServicoItemProdutoCommand(ItemOSProdutoAdicionarDto dto);

    AbrirOrdemServicoItemServicoCommand toAbrirOrdemServicoItemServicoCommand(ItemOSServicoAdicionarDto dto);

    @Mapping(source = "osId", target = "id")
    OrdemServicoResponseDto toResponse(AbrirOrdemServicoOutput output);

    IniciarDiagnosticoCommand toIniciarDiagnosticoCommand(Long osId);

    AdicionarItemServicoCommand toAdicionarItemServicoCommand(Long osId, @Valid ItemOSServicoAdicionarDto request);

    AlterarItemServicoCommand toAlterarItemServicoCommand(Long osId, Long itemId, @Valid ItemOSServicoAlterarDto dto);

    IniciarExecucaoItemServicoCommand toIniciarExecucaoItemServicoCommand(Long osId, Long itemId);

    AdicionarItemProdutoCommand toAdicionarItemProdutoCommand(Long osId, @Valid ItemOSProdutoAdicionarDto request);

    AlterarItemProdutoCommand toAlterarItemProdutoCommand(Long osId, Long itemId, @Valid ItemOSProdutoAlterarDto dto);

    ConcluirDiagnosticoCommand toConcluirDiagnosticoCommand(Long osId, @Valid ConcluirDiagnosticoDto request);

    EnviarOrcamentoCommand toEnviarOrcamentoCommand(Long osId);

    LinkWhatsAppDto toLinkWhatsAppResponse(EnviarOrcamentoOutput enviarOrcamentoOutput);

    ReprovarOrcamentoCommand toReprovarOrcamentoCommand(Long osId);

    AprovarOrcamentoCommand toAprovarOrcamentoCommand(Long osId);

    VerificarEstoqueCommand toVerificarEstoqueCommand(Long osId);

    VerificarEstoqueResponseDto toVerificarEstoqueResponse(VerificarEstoqueOutput output);

    IniciarExecucaoCommand toIniciarExecucaoCommand(Long osId);

    ConcluirExecucaoItemServicoCommand toConcluirExecucaoItemServicoCommand(Long osId, Long itemId, @Valid ConcluirExecucaoItemServicoDto request);

    FinalizarExecucaoCommand toFinalizarExecucaoCommand(Long osId);

    BuscarOSPorIdQuery toBuscarOSPorIdQuery(Long osId);

    BuscarOSPorIdResponseDto toBuscarOSPorIdResponse(BuscarOSPorIdOuput output);

    BuscarOSPorCodigoRastreioQuery toBuscarOSPorCodigoRastreioQuery(String codigoRastreio, Long clienteId);

    BuscarOSPorCodigoRastreioResponseDto toBuscarOSPorCodigoRastreioResponse(BuscarOSPorCodigoRastreioOuput output);

    ConcluirOrdemServicoCommand toConcluirOrdemServicoCommand(Long osId);

    DecisaoClienteCommand toProcessarDecisaoClienteCommand(String codigoRastreio, @Valid DecisaoClienteRequestDto request);

    AtualizarStatusCommand toAtualizarStatusCommand(String codigoRastreio, @Valid AtualizarStatusDto request);

    FilaTrabalhosResponseDto toListarAtivasRespose(ListarFilaTrabalhoOutput output);
}