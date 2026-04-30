package br.com.contadin.infrastructure.persistence.repository.usuario;

import br.com.contadin.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, UUID> {
    boolean existsByEmail(String email);

    Optional<UsuarioEntity> findByEmail(String email);
}
