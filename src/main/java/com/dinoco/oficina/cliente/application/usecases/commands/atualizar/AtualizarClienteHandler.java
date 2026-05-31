package com.dinoco.oficina.cliente.application.usecases.commands.atualizar;

import com.dinoco.oficina.cliente.application.gateways.ClienteCommandGateway;
import com.dinoco.oficina.cliente.domain.Cliente;
import com.dinoco.oficina.cliente.domain.Endereco;
import com.dinoco.oficina.exception.RecursoNaoEncontradoException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AtualizarClienteHandler implements AtualizarClienteUseCase {

    private final ClienteCommandGateway clienteCommandGateway;

    // Injeta apenas o gateway de escrita (Command)
    public AtualizarClienteHandler(ClienteCommandGateway clienteCommandGateway) {
        this.clienteCommandGateway = clienteCommandGateway;
    }

    @Override
    public AtualizarClienteOutput executar(AtualizarClienteCommand command) {

        Cliente cliente = clienteCommandGateway.buscarParaAlteracao(command.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));

        if (!cliente.getDocumento().equals(command.documento())) {
            throw new IllegalArgumentException("Não é permitido alterar o documento (CPF/CNPJ) de um cliente já cadastrado.");
        }

        if (!cliente.getTipoPessoa().equals(command.tipoPessoa())) {
            throw new IllegalArgumentException("Não é permitido alterar o tipo de pessoa após o cadastro.");
        }

        cliente.atualizarDados(
                command.nome(),
                command.inscricaoEstadual(),
                command.nomeFantasia(),
                command.email(),
                command.telefone()
        );

        if (command.enderecos() != null) {
            List<Endereco> novosEnderecos = command.enderecos().stream()
                    .map(endCmd -> new Endereco(
                            endCmd.cep(), endCmd.logradouro(), endCmd.numero(),
                            endCmd.complemento(), endCmd.bairro(), endCmd.cidade(), endCmd.uf()
                    )).collect(Collectors.toList());

            cliente.substituirEnderecos(novosEnderecos);
        } else {
            cliente.substituirEnderecos(new ArrayList<>());
        }

        Cliente clienteSalvo = clienteCommandGateway.salvar(cliente);

        return mapearParaOutput(clienteSalvo);

    }

    private AtualizarClienteOutput mapearParaOutput(Cliente cliente) {
        List<EnderecoOutput> enderecosOutput = cliente.getEnderecos().stream()
                .map(end -> new EnderecoOutput(
                        end.getCep(), end.getLogradouro(), end.getNumero(),
                        end.getComplemento(), end.getBairro(), end.getCidade(), end.getUf()
                )).collect(Collectors.toList());

        return new AtualizarClienteOutput(
                cliente.getId(),
                cliente.getTipoPessoa(),
                cliente.getDocumento(),
                cliente.getInscricaoEstadual(),
                cliente.getNome(),
                cliente.getNomeFantasia(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getAtivo(),
                enderecosOutput
        );
    }
}
