package com.dinoco.oficina.service;

import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.repository.VeiculoRepository;
import com.dinoco.oficina.veiculo.infrastructure.web.dto.VeiculoRequestDto;
import com.dinoco.oficina.veiculo.infrastructure.web.dto.VeiculoResponseDto;
import com.dinoco.oficina.entity.Veiculo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VeiculoService {

    private final VeiculoRepository repository;
    private static final String MSG_VEICULO_NAO_ENCONTRADO = "Veículo não encontrado.";

    @Transactional
    public VeiculoResponseDto criar(VeiculoRequestDto dto) {

        if (repository.existsByPlaca(dto.placa())) {
            throw new IllegalArgumentException("Veículo já cadastrado com esta placa.");
        }
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(dto.placa());
        veiculo.setMarca(dto.marca());
        veiculo.setModelo(dto.modelo());
        veiculo.setAnoFabricacao(dto.anoFabricacao());
        veiculo.setAnoModelo(dto.anoModelo());
        veiculo.setCor(dto.cor());
        veiculo.setChassi(dto.chassi());
        veiculo.setMotor(dto.motor());
        Veiculo veiculoSalvo = repository.save(veiculo);
        return mapearParaResponse(veiculoSalvo);
    }

    public VeiculoResponseDto buscarPorId(Long id) {
        Veiculo veiculo = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(MSG_VEICULO_NAO_ENCONTRADO));
        return mapearParaResponse(veiculo);
    }

    public Veiculo buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado com ID: " + id));
    }

    @Transactional
    public VeiculoResponseDto atualizar(Long id, VeiculoRequestDto dto) {
        Veiculo veiculo = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(MSG_VEICULO_NAO_ENCONTRADO));

        if (!veiculo.getPlaca().equalsIgnoreCase(dto.placa()) && repository.existsByPlaca(dto.placa())) {
            throw new IllegalArgumentException("Já existe outro veículo cadastrado com esta placa.");
        }

        veiculo.setPlaca(dto.placa());
        veiculo.setMarca(dto.marca());
        veiculo.setModelo(dto.modelo());
        veiculo.setAnoFabricacao(dto.anoFabricacao());
        veiculo.setAnoModelo(dto.anoModelo());
        veiculo.setCor(dto.cor());
        veiculo.setChassi(dto.chassi());
        veiculo.setMotor(dto.motor());
        Veiculo veiculoAtualizado = repository.save(veiculo);
        return mapearParaResponse(veiculoAtualizado);
    }

    @Transactional
    public void desativar(Long id) {
        Veiculo veiculo = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(MSG_VEICULO_NAO_ENCONTRADO));
        veiculo.setAtivo(false);
        repository.save(veiculo);
    }

    private VeiculoResponseDto mapearParaResponse(Veiculo veiculo) {
        return new VeiculoResponseDto(
                veiculo.getId(),
                veiculo.getPlaca(),
                veiculo.getMarca(),
                veiculo.getModelo(),
                veiculo.getAnoFabricacao(),
                veiculo.getAnoModelo(),
                veiculo.getCor(),
                veiculo.getChassi(),
                veiculo.getMotor(),
                veiculo.getAtivo()
        );
    }
}
