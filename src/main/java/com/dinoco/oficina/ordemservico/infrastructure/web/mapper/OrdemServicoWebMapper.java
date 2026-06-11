package com.dinoco.oficina.ordemservico.infrastructure.web.mapper;

import com.dinoco.oficina.ordemservico.application.usecases.commands.abrir.AbrirOrdemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.abrir.AbrirOrdemServicoOutput;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemproduto.AdicionarItemProdutoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.adicionaritemservico.AdicionarItemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemproduto.AlterarItemProdutoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.alteraritemservico.AlterarItemServicoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.aprovar.AprovarOrcamentoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirdiagnostico.ConcluirDiagnosticoCommand;
import com.dinoco.oficina.ordemservico.application.usecases.commands.concluirexecucaoitemservico.ConcluirExecucaoItemServicoCommand;
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
import com.dinoco.oficina.ordemservico.infrastructure.web.dto.*;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrdemServicoWebMapper {

    //Converte do DTO da Web para o Input do Core
    AbrirOrdemServicoCommand toInput(@Valid OrdemServicoRequestDto request);

    //Converte do Output do Core para o DTO da Web
    OrdemServicoResponseDto toResponse(AbrirOrdemServicoOutput output);

    IniciarDiagnosticoCommand toIniciarDiagnosticoCommand(Long id);


    AdicionarItemServicoCommand toAdicionarItemServicoCommand(Long id, @Valid ItemOSServicoAdicionarDto request);

    AlterarItemServicoCommand toAlterarItemServicoCommand(Long osId, Long itemId, @Valid ItemOSServicoAlterarDto dto);

    IniciarExecucaoItemServicoCommand toIniciarExecucaoItemServicoCommand(Long osId, Long itemId);


    AdicionarItemProdutoCommand toAdicionarItemProdutoCommand(Long id, @Valid ItemOSProdutoAdicionarDto request);

    AlterarItemProdutoCommand toAlterarItemProdutoCommand(Long osId, Long itemId, @Valid ItemOSProdutoAlterarDto dto);

    ConcluirDiagnosticoCommand toConcluirDiagnosticoCommand(Long id, @Valid ConcluirDiagnosticoDto request);

    EnviarOrcamentoCommand toEnviarOrcamentoCommand(Long id);

    LinkWhatsAppDto toLinkWhatsAppResponse(EnviarOrcamentoOutput enviarOrcamentoOutput);

    ReprovarOrcamentoCommand toReprovarOrcamentoCommand(Long id);

    AprovarOrcamentoCommand toAprovarOrcamentoCommand(Long id);

    VerificarEstoqueCommand toVerificarEstoqueCommand(Long id);

    VerificarEstoqueResponseDto toVerificarEstoqueResponse(VerificarEstoqueOutput output);

    IniciarExecucaoCommand toIniciarExecucaoCommand(Long id);

    ConcluirExecucaoItemServicoCommand toConcluirExecucaoItemServicoCommand(Long osId, Long itemId, @Valid ConcluirExecucaoItemServicoDto request);

    FinalizarExecucaoCommand toFinalizarExecucaoCommand(Long id);

    BuscarOSPorIdQuery toBuscarOSPorIdQuery(Long id);

    BuscarOSPorIdResponseDto toBuscarOSPorIdResponse(BuscarOSPorIdOuput output);

    BuscarOSPorCodigoRastreioQuery toBuscarOSPorCodigoRastreioQuery(String codigoRastreio);

    BuscarOSPorCodigoRastreioResponseDto toBuscarOSPorCodigoRastreioResponse(BuscarOSPorCodigoRastreioOuput output);
}
