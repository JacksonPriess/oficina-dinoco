package com.dinoco.oficina.service;

import com.dinoco.oficina.dto.ItemOSServicoAdicionarDto;
import com.dinoco.oficina.dto.ItemServicoAlterarDto;
import com.dinoco.oficina.entity.Funcionario;
import com.dinoco.oficina.entity.ItemOSServico;
import com.dinoco.oficina.entity.OrdemServico;
import com.dinoco.oficina.entity.Servico;
import com.dinoco.oficina.enums.StatusItemServico;
import com.dinoco.oficina.enums.StatusOS;
import com.dinoco.oficina.repository.ItemOSServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ItemOSServicoService {

    private final ItemOSServicoRepository repository;
    private final OrdemServicoService ordemServicoService;
    private final ServicoService servicoService;
    private final FuncionarioService funcionarioService;

    @Transactional
    public void adicionarItemServico(Long osId, ItemOSServicoAdicionarDto dto) {
        OrdemServico os = ordemServicoService.buscarOuFalhar(osId);
        if ( !os.getStatus().equals(StatusOS.EM_DIAGNOSTICO) ) {
            throw new IllegalArgumentException("Inicie o diagnóstico da OS antes de adicionar itens de serviço.");
        }
        Servico servico = servicoService.buscarEntidadePorId(dto.servicoId());
        ItemOSServico item = new ItemOSServico();
        item.setOrdemServico(os);
        item.setServico(servico);
        item.setValorCobrado(servico.getPrecoPadrao());
        item.setStatusItem(StatusItemServico.PENDENTE);
        if (dto.mecanicoId() != null) {
            //TODO - Garantir que o funcionario é mecanico
            Funcionario mecanico = funcionarioService.buscarEntidadePorId(dto.mecanicoId());
            item.setMecanico(mecanico);
        }
        os.getItensServico().add(item);
        repository.save(item);
        ordemServicoService.recalcularTotais(osId);
    }

    @Transactional
    public void alterarItemServico(Long itemId, ItemServicoAlterarDto dto) {
        ItemOSServico item = buscarItemOuFalhar(itemId);
        OrdemServico os = item.getOrdemServico();
        validarStatusParaEdicao(os);
        item.setValorCobrado(dto.valorCobrado());
        if (dto.mecanicoId() != null) {
            //TODO - Garantir que o funcionario é mecanico
            Funcionario mecanico = funcionarioService.buscarEntidadePorId(dto.mecanicoId());
            item.setMecanico(mecanico);
        } else {
            item.setMecanico(null);
        }
        repository.save(item);
        ordemServicoService.recalcularTotais(os.getId());
    }

    @Transactional
    public void removerItemServico(Long itemId) {
        ItemOSServico item = buscarItemOuFalhar(itemId);
        OrdemServico os = item.getOrdemServico();
        validarStatusParaEdicao(os);
        os.getItensServico().remove(item);
        repository.delete(item);
        ordemServicoService.recalcularTotais(os.getId());
    }

    @Transactional
    public void iniciarExecucaoItemServico(Long itemId) {
        ItemOSServico item = buscarItemOuFalhar(itemId);
        item.setStatusItem(StatusItemServico.EM_ANDAMENTO);
        item.setDataInicio(LocalDateTime.now());
        repository.save(item);
    }

    @Transactional
    public void concluirExecucaoItemServico(Long itemId) {
        ItemOSServico item = buscarItemOuFalhar(itemId);
        item.setStatusItem(StatusItemServico.CONCLUIDO);
        item.setDataFim(LocalDateTime.now());
        repository.save(item);
    }

    private ItemOSServico buscarItemOuFalhar(Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item de serviço não encontrado."));
    }

    private void validarStatusParaEdicao(OrdemServico os) {
        if (os.getStatus() != StatusOS.EM_DIAGNOSTICO && os.getStatus() != StatusOS.AGUARDANDO_ORCAMENTO) {
            throw new IllegalStateException("A OS não permite modificação de serviços no status atual: " + os.getStatus());
        }
    }
}