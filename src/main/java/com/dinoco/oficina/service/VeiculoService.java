package com.dinoco.oficina.service;

import com.dinoco.oficina.repository.ClienteRepository;
import com.dinoco.oficina.repository.VeiculoRepository;
import com.dinoco.oficina.dto.VeiculoRequestDto;
import com.dinoco.oficina.dto.VeiculoResponseDto;
import com.dinoco.oficina.entity.Cliente;
import com.dinoco.oficina.entity.Veiculo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public VeiculoResponseDto criar(VeiculoRequestDto dto) {

        if (veiculoRepository.existsByPlaca(dto.placa())) {
            throw new IllegalArgumentException("Veículo já cadastrado com esta placa.");
        }

        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        Veiculo veiculo = new Veiculo();
        veiculo.setCliente(cliente);
        veiculo.setPlaca(dto.placa());
        veiculo.setMarca(dto.marca());
        veiculo.setModelo(dto.modelo());
        veiculo.setAnoFabricacao(dto.anoFabricacao());
        veiculo.setAnoModelo(dto.anoModelo());
        veiculo.setCor(dto.cor());
        veiculo.setChassi(dto.chassi());
        veiculo.setMotor(dto.motor());
        Veiculo veiculoSalvo = veiculoRepository.save(veiculo);
        return mapearParaResponse(veiculoSalvo);
    }

    public VeiculoResponseDto buscarPorId(Long id) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado."));
        return mapearParaResponse(veiculo);
    }

    @Transactional
    public VeiculoResponseDto atualizar(Long id, VeiculoRequestDto dto) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado."));

        // 1. Valida se a placa foi alterada e se a nova placa já pertence a outro carro
        if (!veiculo.getPlaca().equalsIgnoreCase(dto.placa()) && veiculoRepository.existsByPlaca(dto.placa())) {
            throw new IllegalArgumentException("Já existe outro veículo cadastrado com esta placa.");
        }

        // 2. Valida se o dono do carro mudou (Transferência de propriedade)
        if (!veiculo.getCliente().getId().equals(dto.clienteId())) {
            Cliente novoCliente = clienteRepository.findById(dto.clienteId())
                    .orElseThrow(() -> new IllegalArgumentException("Novo cliente não encontrado."));
            veiculo.setCliente(novoCliente);
        }

        // 3. Atualiza os demais dados
        veiculo.setPlaca(dto.placa());
        veiculo.setMarca(dto.marca());
        veiculo.setModelo(dto.modelo());
        veiculo.setAnoFabricacao(dto.anoFabricacao());
        veiculo.setAnoModelo(dto.anoModelo());
        veiculo.setCor(dto.cor());
        veiculo.setChassi(dto.chassi());
        veiculo.setMotor(dto.motor());

        Veiculo veiculoAtualizado = veiculoRepository.save(veiculo);
        return mapearParaResponse(veiculoAtualizado);
    }

    @Transactional
    public void desativar(Long id) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado."));
        veiculo.setAtivo(false);
        veiculoRepository.save(veiculo);
    }

    private VeiculoResponseDto mapearParaResponse(Veiculo veiculo) {
        return new VeiculoResponseDto(
                veiculo.getId(),
                veiculo.getCliente().getId(),
                veiculo.getCliente().getNome(), // Pega o nome para o DTO
                veiculo.getPlaca(),
                veiculo.getMarca(),
                veiculo.getModelo(),
                veiculo.getAnoFabricacao(),
                veiculo.getAnoModelo(),
                veiculo.getCor(),
                veiculo.getChassi(),
                veiculo.getMotor()
        );
    }
}
