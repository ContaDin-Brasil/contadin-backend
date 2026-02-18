package br.com.contadin.infrastructure.persistence.repository.usuario;

import br.com.contadin.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Integer> {
    boolean existsByEmail(String email);
}
