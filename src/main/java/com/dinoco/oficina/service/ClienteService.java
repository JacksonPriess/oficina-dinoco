package com.dinoco.oficina.service;

import com.dinoco.oficina.dto.ClienteRequestDto;
import com.dinoco.oficina.dto.ClienteResponseDto;
import com.dinoco.oficina.dto.EnderecoDto;
import com.dinoco.oficina.entity.Cliente;
import com.dinoco.oficina.entity.Endereco;
import com.dinoco.oficina.repository.ClienteRepository;
import com.dinoco.oficina.util.DocumentoUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository){
        this.repository = repository;
    }

    @Transactional
    public ClienteResponseDto criar(ClienteRequestDto dto) {

        // 1. Validação Lógica de CPF/CNPJ
        if ("F".equals(dto.tipoPessoa()) && !DocumentoUtil.isCpfValido(dto.documento())) {
            throw new IllegalArgumentException("CPF inválido.");
        }

        if ("J".equals(dto.tipoPessoa()) && !DocumentoUtil.isCnpjValido(dto.documento())) {
            throw new IllegalArgumentException("CNPJ inválido.");
        }

        if (repository.existsByDocumento(dto.documento())) {
            throw new IllegalArgumentException("Cliente já cadastrado com este documento.");
        }

        Cliente cliente = new Cliente();
        cliente.setTipoPessoa(dto.tipoPessoa());
        cliente.setDocumento(dto.documento());
        cliente.setInscricaoEstadual(dto.inscricaoEstadual());
        cliente.setNome(dto.nome());
        cliente.setNomeFantasia(dto.nomeFantasia());
        cliente.setEmail(dto.email());
        cliente.setTelefone(dto.telefone());

        if (dto.enderecos() != null) {
            for (EnderecoDto endDto : dto.enderecos()) {
                Endereco endereco = new Endereco();
                endereco.setCep(endDto.cep());
                endereco.setLogradouro(endDto.logradouro());
                endereco.setNumero(endDto.numero());
                endereco.setComplemento(endDto.complemento());
                endereco.setBairro(endDto.bairro());
                endereco.setCidade(endDto.cidade());
                endereco.setUf(endDto.uf());

                cliente.addEndereco(endereco);
            }
        }

        Cliente clienteSalvo = repository.save(cliente);
        return mapearParaResponse(clienteSalvo);
    }

    public ClienteResponseDto buscarPorId(Long id) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
        return mapearParaResponse(cliente);
    }

    // Método privado para mapear Entidade -> Response DTO
    private ClienteResponseDto mapearParaResponse(Cliente cliente) {
        List<EnderecoDto> enderecosDto = cliente.getEnderecos().stream()
                .map(end -> new EnderecoDto(
                        end.getCep(), end.getLogradouro(), end.getNumero(),
                        end.getComplemento(), end.getBairro(), end.getCidade(), end.getUf()
                )).toList();

        return new ClienteResponseDto(
                cliente.getId(), cliente.getTipoPessoa(), cliente.getDocumento(), cliente.getInscricaoEstadual(),
                cliente.getNome(), cliente.getNomeFantasia(), cliente.getEmail(), cliente.getTelefone(), enderecosDto
        );
    }
}
