package com.dinoco.oficina.service;

import com.dinoco.oficina.dto.ClienteRequestDto;
import com.dinoco.oficina.dto.ClienteResponseDto;
import com.dinoco.oficina.dto.EnderecoDto;
import com.dinoco.oficina.entity.Cliente;
import com.dinoco.oficina.entity.Endereco;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import com.dinoco.oficina.repository.ClienteRepository;
import com.dinoco.oficina.util.DocumentoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;

    @Transactional
    public ClienteResponseDto criar(ClienteRequestDto dto) {

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
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));
        return mapearParaResponse(cliente);
    }

    public Cliente buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado com ID: " + id));
    }

    @Transactional
    public ClienteResponseDto atualizar(Long id, ClienteRequestDto dto) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));

        if (!cliente.getDocumento().equals(dto.documento())) {
            throw new IllegalArgumentException("Não é permitido alterar o documento (CPF/CNPJ) de um cliente já cadastrado.");
        }
        if (!cliente.getTipoPessoa().equals(dto.tipoPessoa())) {
            throw new IllegalArgumentException("Não é permitido alterar o tipo de pessoa após o cadastro.");
        }

        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());
        cliente.setTelefone(dto.telefone());
        cliente.getEnderecos().clear();

        if (dto.enderecos() != null) {
            dto.enderecos().forEach(endDto -> {
                Endereco novoEndereco = new Endereco();
                novoEndereco.setCep(endDto.cep());
                novoEndereco.setLogradouro(endDto.logradouro());
                novoEndereco.setNumero(endDto.numero());
                novoEndereco.setBairro(endDto.bairro());
                novoEndereco.setComplemento(endDto.complemento());
                novoEndereco.setCidade(endDto.cidade());
                novoEndereco.setUf(endDto.uf());

                // Amarração bidirecional essencial
                novoEndereco.setCliente(cliente);
                cliente.getEnderecos().add(novoEndereco);
            });
        }

        Cliente atualizado = repository.save(cliente);
        return mapearParaResponse(atualizado);
    }

    @Transactional
    public void desativar(Long id) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));

        cliente.setAtivo(false);
        repository.save(cliente);
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
                cliente.getNome(), cliente.getNomeFantasia(), cliente.getEmail(), cliente.getTelefone(), cliente.getAtivo(), enderecosDto
        );
    }
}
