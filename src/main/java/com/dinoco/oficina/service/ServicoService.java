package com.dinoco.oficina.service;

import com.dinoco.oficina.dto.ServicoRequestDto;
import com.dinoco.oficina.dto.ServicoResponseDto;
import com.dinoco.oficina.entity.Servico;
import com.dinoco.oficina.repository.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicoService {

    private final ServicoRepository repository;

    public ServicoService(ServicoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ServicoResponseDto criar(ServicoRequestDto dto) {
        if (repository.existsByDescricaoIgnoreCase(dto.descricao())) {
            throw new IllegalArgumentException("Já existe um serviço cadastrado com este nome.");
        }

        Servico servico = new Servico();
        servico.setDescricao(dto.descricao());
        servico.setPrecoPadrao(dto.precoPadrao());
        servico.setTempoEstimadoMinutos(dto.tempoEstimadoMinutos());

        Servico salvo = repository.save(servico);
        return mapearParaResponse(salvo);
    }

    public ServicoResponseDto buscarPorId(Long id) {
        Servico servico = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado."));
        return mapearParaResponse(servico);
    }

    @Transactional
    public ServicoResponseDto atualizar(Long id, ServicoRequestDto dto) {
        Servico servico = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado."));

        // Valida se o usuário mudou a descrição do serviço para um descrição que já existe em outro registro
        if (!servico.getDescricao().equalsIgnoreCase(dto.descricao()) && repository.existsByDescricaoIgnoreCase(dto.descricao())) {
            throw new IllegalArgumentException("Já existe outro serviço cadastrado com esta descrição.");
        }

        servico.setDescricao(dto.descricao());
        servico.setPrecoPadrao(dto.precoPadrao());
        servico.setTempoEstimadoMinutos(dto.tempoEstimadoMinutos());
        Servico atualizado = repository.save(servico);
        return mapearParaResponse(atualizado);
    }

    @Transactional
    public void desativar(Long id) {
        Servico servico = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado."));

        servico.setAtivo(false);
        repository.save(servico);
    }

    private ServicoResponseDto mapearParaResponse(Servico servico) {
        return new ServicoResponseDto(
                servico.getId(),
                servico.getDescricao(),
                servico.getPrecoPadrao(),
                servico.getTempoEstimadoMinutos(),
                servico.getAtivo()
        );
    }
}