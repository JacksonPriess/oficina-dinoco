package com.dinoco.oficina.cliente.application.usecases.commands.criar;

import com.dinoco.oficina.cliente.application.gateways.ClienteCommandGateway;
import com.dinoco.oficina.cliente.application.gateways.ClienteQueryGateway;
import com.dinoco.oficina.cliente.domain.Cliente;
import com.dinoco.oficina.cliente.domain.Endereco;
import java.util.List;

public class CriarClienteHandler implements CriarClienteUseCase {

    private final ClienteCommandGateway clienteCommandGateway;
    private final ClienteQueryGateway clienteQueryGateway;

    public CriarClienteHandler(ClienteCommandGateway clienteCommandGateway, ClienteQueryGateway clienteQueryGateway) {
        this.clienteCommandGateway = clienteCommandGateway;
        this.clienteQueryGateway = clienteQueryGateway;
    }

    @Override
    public CriarClienteOutput executar(CriarClienteCommand command) {

        if (clienteQueryGateway.existePorDocumento(command.documento())) {
            throw new IllegalArgumentException("Cliente já cadastrado com este documento.");
        }

        Cliente novoCliente = new Cliente(
                command.tipoPessoa(),
                command.documento(),
                command.nome(),
                command.inscricaoEstadual(),
                command.nomeFantasia(),
                command.email(),
                command.telefone()
        );

        if (command.enderecos() != null) {
            for (EnderecoCommand endInput : command.enderecos()) {
                Endereco endereco = new Endereco(
                        endInput.cep(), endInput.logradouro(), endInput.numero(),
                        endInput.complemento(), endInput.bairro(), endInput.cidade(), endInput.uf()
                );
                novoCliente.adicionarEndereco(endereco);
            }
        }

        Cliente clienteSalvo = clienteCommandGateway.salvar(novoCliente);

        return mapearParaOutput(clienteSalvo);
    }

    private CriarClienteOutput mapearParaOutput(Cliente cliente) {
        List<EnderecoOutput> enderecosOutput = cliente.getEnderecos().stream()
                .map(end -> new EnderecoOutput(
                        end.getCep(), end.getLogradouro(), end.getNumero(),
                        end.getComplemento(), end.getBairro(), end.getCidade(), end.getUf()
                )).toList();

        return new CriarClienteOutput(
                cliente.getId(), cliente.getTipoPessoa(), cliente.getDocumento(),
                cliente.getInscricaoEstadual(), cliente.getNome(), cliente.getNomeFantasia(),
                cliente.getEmail(), cliente.getTelefone(), cliente.getAtivo(), enderecosOutput
        );
    }
}