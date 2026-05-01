package br.com.contadin.infrastructure.persistence.repository.objetivo;

import br.com.contadin.infrastructure.persistence.entity.ObjetivoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface ObjetivoJpaRepository extends JpaRepository<ObjetivoEntity, UUID> {
    List<ObjetivoEntity> findByFkUsuario(UUID fkUsuario);
    List<ObjetivoEntity> findByFkUsuarioAndNome(UUID fkUsuario, String nome);
}
