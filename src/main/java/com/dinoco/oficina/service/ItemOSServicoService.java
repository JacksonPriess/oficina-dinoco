package com.dinoco.oficina.service;
/*
import com.dinoco.oficina.aordemservico.infrastructure.web.dto.ItemOSServicoAdicionarDto;
import com.dinoco.oficina.aordemservico.infrastructure.web.dto.ItemOSServicoAlterarDto;
import com.dinoco.oficina.dto.OrdemServicoDetalhadaResponseDto;
import com.dinoco.oficina.entity.Funcionario;
import com.dinoco.oficina.entity.ItemOSServico;
import com.dinoco.oficina.entity.OrdemServico;
import com.dinoco.oficina.entity.Servico;
import com.dinoco.oficina.enums.StatusItemServico;
import com.dinoco.oficina.enums.StatusOS;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
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
    public OrdemServicoDetalhadaResponseDto adicionarItemServico(Long osId, ItemOSServicoAdicionarDto dto) {
        OrdemServico os = ordemServicoService.buscarOuFalhar(osId);
        if ( !os.getStatus().equals(StatusOS.EM_DIAGNOSTICO) ) {
            throw new IllegalArgumentException("Inicie o diagnóstico da OS antes de adicionar itens de serviço.");
        }
        if (repository.existsByOrdemServicoIdAndServicoId(osId, dto.servicoId())) {
            throw new IllegalArgumentException("Este serviço já foi adicionado a esta Ordem de Serviço.");
        }
        Servico servico = servicoService.buscarEntidadePorId(dto.servicoId());
        ItemOSServico item = new ItemOSServico();
        item.setOrdemServico(os);
        item.setServico(servico);
        item.setValorCobrado(servico.getPrecoPadrao());
        item.setStatusItem(StatusItemServico.PENDENTE);
        if (dto.mecanicoId() != null) {
            Funcionario mecanico = funcionarioService.buscarEntidadePorId(dto.mecanicoId());
            item.setMecanico(mecanico);
        }
        os.getItensServico().add(item);
        repository.save(item);
        ordemServicoService.recalcularTotais(osId);
        return ordemServicoService.buscarDetalhesPorCodigoRastreio(os.getCodigoRastreio());
    }

    @Transactional
    public void alterarItemServico(Long itemId, ItemOSServicoAlterarDto dto) {
        ItemOSServico item = buscarItemOuFalhar(itemId);
        OrdemServico os = item.getOrdemServico();
        validarStatusParaEdicao(os);
        item.setValorCobrado(dto.valorCobrado() != null ? dto.valorCobrado() : item.getValorCobrado() );
        if (dto.mecanicoId() != null) {
            Funcionario mecanico = funcionarioService.buscarEntidadePorId(dto.mecanicoId());
            item.setMecanico(mecanico);
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
        if (item.getStatusItem() != StatusItemServico.PENDENTE) {
            throw new IllegalArgumentException(
                    String.format("Não é possível iniciar a execução. O serviço está com status %s, mas é exigido o status PENDENTE.", item.getStatusItem())
            );
        }
        item.setStatusItem(StatusItemServico.EM_ANDAMENTO);
        item.setDataInicio(LocalDateTime.now());
        repository.save(item);
    }

    @Transactional
    public void concluirExecucaoItemServico(Long itemId, LocalDateTime dataFimManual) {
        ItemOSServico item = buscarItemOuFalhar(itemId);
        if (item.getStatusItem() != StatusItemServico.EM_ANDAMENTO) {
            throw new IllegalArgumentException(
                    String.format("Não é possível concluir a execução. O serviço está com status %s, mas é exigido o status EM_ANDAMENTO.", item.getStatusItem())
            );
        }
        LocalDateTime dataConclusao = dataFimManual != null ? dataFimManual : LocalDateTime.now();
        if (item.getDataInicio() != null && dataConclusao.isBefore(item.getDataInicio())) {
            throw new IllegalArgumentException("A data de conclusão não pode ser anterior à data de início do serviço.");
        }
        item.setStatusItem(StatusItemServico.CONCLUIDO);
        item.setDataFim(dataConclusao);
        repository.save(item);
    }

    private ItemOSServico buscarItemOuFalhar(Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item de serviço não encontrado."));
    }

    private void validarStatusParaEdicao(OrdemServico os) {
        if (os.getStatus() != StatusOS.EM_DIAGNOSTICO && os.getStatus() != StatusOS.AGUARDANDO_ORCAMENTO) {
            throw new IllegalStateException("A OS não permite modificação em itens de serviços no status atual: " + os.getStatus());
        }
    }

}

 */