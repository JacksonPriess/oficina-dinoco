package com.dinoco.oficina.funcionario.infrastructure.persistence;

import com.dinoco.oficina.autenticacao.infrastructure.persistence.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {

    boolean existsByLogin(String login);

    UserDetails findByLogin(String login);

}
