package com.dinoco.oficina.funcionario.infrastructure.persistence;

import com.dinoco.oficina.funcionario.domain.CargoFuncionario;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "funcionario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class FuncionarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CargoFuncionario cargo;

    private boolean ativo = true;

    @Column(name = "usuario_id")
    private Long usuarioId;

}
