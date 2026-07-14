package com.dinoco.oficina.cliente.infrastructure.gateways;

import com.dinoco.oficina.cliente.application.gateways.ClienteQueryGateway;
import com.dinoco.oficina.cliente.application.usecases.queries.buscarporid.BuscarClientePorIdOutput;
import com.dinoco.oficina.cliente.application.usecases.queries.buscarporid.EnderecoQueryOutput;
import com.dinoco.oficina.cliente.infrastructure.persistence.ClienteEntity;
import com.dinoco.oficina.cliente.infrastructure.persistence.ClienteJpaRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ClienteQueryGatewayImpl implements ClienteQueryGateway {
    private final ClienteJpaRepository jpaRepository;

    public ClienteQueryGatewayImpl(ClienteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existePorDocumento(String documento) {
        return jpaRepository.existsByDocumento(documento);
    }

    @Override
    public Optional<BuscarClientePorIdOutput> buscarDetalhesPorId(Long id) {
        return jpaRepository.findById(id).map(this::mapearParaOutputVisual);
    }

    private BuscarClientePorIdOutput mapearParaOutputVisual(ClienteEntity entity) {

        List<EnderecoQueryOutput> enderecosOutput = entity.getEnderecos().stream().map(end ->
                new EnderecoQueryOutput(
                        end.getCep(),
                        end.getLogradouro(),
                        end.getNumero(),
                        end.getComplemento(),
                        end.getBairro(),
                        end.getCidade(),
                        end.getUf()
                )
        ).collect(Collectors.toList());

        // Devolve o Record DTO que a UseCase/Query exige
        return new BuscarClientePorIdOutput(
                entity.getId(),
                entity.getTipoPessoa(),
                entity.getDocumento(),
                entity.getNome(),
                entity.getInscricaoEstadual(),
                entity.getNomeFantasia(),
                entity.getEmail(),
                entity.getTelefone(),
                entity.getAtivo(),
                entity.getDataCriacao(),
                enderecosOutput
        );
    }
}
