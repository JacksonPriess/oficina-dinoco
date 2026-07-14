package com.dinoco.oficina.autenticacao.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    boolean existsByLogin(String login);

    UserDetails findByLogin(String login);

}
