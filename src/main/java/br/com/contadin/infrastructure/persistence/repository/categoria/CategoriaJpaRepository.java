package br.com.contadin.infrastructure.persistence.repository.categoria;

import br.com.contadin.infrastructure.persistence.entity.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoriaJpaRepository extends JpaRepository<CategoriaEntity, UUID> {
}
