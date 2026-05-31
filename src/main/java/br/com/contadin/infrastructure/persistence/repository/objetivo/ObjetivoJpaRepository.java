package br.com.contadin.infrastructure.persistence.repository.objetivo;

import br.com.contadin.infrastructure.persistence.entity.ObjetivoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ObjetivoJpaRepository extends JpaRepository<ObjetivoEntity, UUID> {
    List<ObjetivoEntity> findByFkUsuario(UUID fkUsuario);

    List<ObjetivoEntity> findByFkUsuarioAndNomeContainingIgnoreCase(UUID fkUsuario, String nome);
}
